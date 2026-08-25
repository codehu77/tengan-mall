package com.tengan.mall.search.application;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.FieldCollapse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

/**
 * 搜尋+聚合合併成一支查詢——Repository 介面對巢狀聚合支援有限，這裡直接用 ElasticsearchOperations
 * + NativeQuery 手動組 bool query + 聚合。catId 不管前端點第幾層分類，都用同一個邏輯比對
 * catalog1Id/catalog2Id/catalog3Id 三個欄位（bool should），對齊「不用應用層展開分類樹」的設計。
 */
@Service
public class SearchSkusService implements SearchSkusUseCase {

    private static final int BRAND_AGG_SIZE = 30;
    private static final int ATTR_ID_AGG_SIZE = 30;
    private static final int ATTR_VALUE_AGG_SIZE = 30;

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchSkusService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    private static final String SORT_DEFAULT = "default";

    @Override
    public SearchSkusResult search(SearchSkusQuery query) {
        Query baseQuery = buildQuery(query);
        // 「綜合排序」（sort=default/null）以前是 Sort.unsorted()——沒有關鍵字時全部文件 _score
        // 都是 1.0，等於完全沒有排序。這裡包一層 function_score，用 saleCount 做加權：有關鍵字時
        // 是「文字相關度 + 銷量」的複合分數，沒有關鍵字時退化成純粹依銷量排序。sale/price 兩種
        // 明確指定的排序不套用這層加權，維持原本的欄位排序邏輯。
        String sortKey = query.sort() == null ? SORT_DEFAULT : query.sort();
        Query esQuery = SORT_DEFAULT.equals(sortKey) ? withSaleCountBoost(baseQuery) : baseQuery;

        // field collapse 依 spuId 分組，一顆 SPU 只回傳一筆代表 hit（列表卡片是商品層級，不是每個
        // SKU 變體各自一張卡）——同一 SPU 底下所有 SKU 依照後台建立商品的慣例本來就同價（見
        // backend_dev_plan 08-06 的 MOMO 討論），代表 hit 選哪一顆不影響顯示的價格，維持原本排序
        // 邏輯（不含關鍵字時走相關度）即可，不用另外為了「選代表」而覆蓋使用者的排序選擇。
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(esQuery)
                .withPageable(PageRequest.of(Math.max(query.page() - 1, 0), query.pageSize(), buildSort(query)))
                .withFieldCollapse(FieldCollapse.of(fc -> fc.field("spuId")))
                .withAggregation("brand_agg", buildBrandAggregation())
                .withAggregation("attr_agg", buildAttrAggregation())
                .withAggregation("spu_total_agg", buildSpuTotalAggregation())
                .build();

        SearchHits<SkuSearchDocument> hits = elasticsearchOperations.search(nativeQuery, SkuSearchDocument.class);

        List<SkuSearchItem> items = hits.getSearchHits().stream().map(this::toItem).toList();
        Map<String, Aggregate> aggs = extractAggregates(hits);
        SearchAggregations aggregations = toAggregations(aggs);
        // collapse 後 hits.getTotalHits() 還是符合查詢條件的 SKU 文件數，不是 distinct SPU
        // 數——分頁筆數要準確，總數必須另外用 cardinality aggregation 算，不能沿用舊的
        // hits.getTotalHits()。
        long total = extractSpuTotal(aggs, hits.getTotalHits());

        return new SearchSkusResult(items, total, query.page(), query.pageSize(), aggregations);
    }

    private Aggregation buildSpuTotalAggregation() {
        return Aggregation.of(a -> a.cardinality(c -> c.field("spuId")));
    }

    private Query buildQuery(SearchSkusQuery query) {
        BoolQuery.Builder bool = new BoolQuery.Builder();

        if (query.keyword() != null && !query.keyword().isBlank()) {
            bool.must(m -> m.multiMatch(mm -> mm.query(query.keyword()).fields("skuName", "spuName")));
        }

        if (query.brandIds() != null && !query.brandIds().isEmpty()) {
            List<FieldValue> brandValues = query.brandIds().stream().map(FieldValue::of).toList();
            bool.filter(f -> f.terms(t -> t.field("brandId").terms(tt -> tt.value(brandValues))));
        }

        if (query.catId() != null) {
            bool.filter(f -> f.bool(b -> b.should(
                            s -> s.term(t -> t.field("catalog1Id").value(query.catId())))
                    .should(s -> s.term(t -> t.field("catalog2Id").value(query.catId())))
                    .should(s -> s.term(t -> t.field("catalog3Id").value(query.catId())))
                    .minimumShouldMatch("1")));
        }

        if (query.attrs() != null) {
            for (Map.Entry<String, List<String>> entry : query.attrs().entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    continue;
                }
                List<FieldValue> values = entry.getValue().stream().map(FieldValue::of).toList();
                bool.filter(f -> f.nested(n -> n.path("attrs")
                        .query(nq -> nq.bool(nb -> nb
                                .must(nm -> nm.term(t -> t.field("attrs.attrKey").value(entry.getKey())))
                                .must(nm -> nm.terms(t -> t.field("attrs.attrValue").terms(tt -> tt.value(values))))))));
            }
        }

        return Query.of(q -> q.bool(bool.build()));
    }

    /**
     * v1 範圍：只做「文字相關度 + saleCount」的複合分數，不含新鮮度因子（ES 文件沒有 createdAt
     * 欄位，之後有需要再加）。field_value_factor 用 log1p 避免銷量差距線性放大成極端分數，
     * boostMode=Sum 讓有關鍵字時的文字相關度分數不會被銷量加權整個蓋過。
     */
    private Query withSaleCountBoost(Query boolQuery) {
        return Query.of(q -> q.functionScore(fs -> fs
                .query(boolQuery)
                .functions(f -> f.fieldValueFactor(fv -> fv
                        .field("saleCount")
                        .modifier(FieldValueFactorModifier.Log1p)
                        .factor(1.0)
                        .missing(0.0)))
                .boostMode(FunctionBoostMode.Sum)));
    }

    private Sort buildSort(SearchSkusQuery query) {
        String field = switch (query.sort() == null ? "default" : query.sort()) {
            case "sale" -> "saleCount";
            case "price" -> "price";
            default -> null;
        };
        if (field == null) {
            return Sort.unsorted();
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(query.order()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private Aggregation buildBrandAggregation() {
        return Aggregation.of(a -> a
                .terms(t -> t.field("brandId").size(BRAND_AGG_SIZE))
                .aggregations("brand_name", Aggregation.of(a2 -> a2.terms(t2 -> t2.field("brandName").size(1)))));
    }

    private Aggregation buildAttrAggregation() {
        return Aggregation.of(a -> a
                .nested(n -> n.path("attrs"))
                .aggregations("by_attr_key", Aggregation.of(a2 -> a2
                        .terms(t -> t.field("attrs.attrKey").size(ATTR_ID_AGG_SIZE))
                        .aggregations("attr_name",
                                Aggregation.of(a3 -> a3.terms(t3 -> t3.field("attrs.attrName").size(1))))
                        .aggregations("by_value",
                                Aggregation.of(a4 -> a4.terms(t4 -> t4.field("attrs.attrValue").size(ATTR_VALUE_AGG_SIZE)))))));
    }

    private SkuSearchItem toItem(SearchHit<SkuSearchDocument> hit) {
        SkuSearchDocument d = hit.getContent();
        // 列表卡片是 collapse 後的商品層級呈現，圖片用 SPU 主圖，不是代表 hit 剛好選中的那顆 SKU 的圖；
        // spuMainImage 是後補欄位，舊資料重建索引前可能還沒有值，退回 sku 自己的圖當備援。
        String image = (d.getSpuMainImage() != null && !d.getSpuMainImage().isBlank())
                ? d.getSpuMainImage() : d.getMainImage();
        return new SkuSearchItem(d.getSkuId(), d.getSpuId(), d.getSkuName(), d.getSpuName(), d.getPrice(), image,
                d.getSaleCount() == null ? 0 : d.getSaleCount(), d.getBrandId(), d.getBrandName());
    }

    private Map<String, Aggregate> extractAggregates(SearchHits<SkuSearchDocument> hits) {
        AggregationsContainer<?> container = hits.getAggregations();
        if (container instanceof ElasticsearchAggregations esAggs) {
            return esAggs.aggregations().stream()
                    .collect(java.util.stream.Collectors.toMap(a -> a.aggregation().getName(),
                            a -> a.aggregation().getAggregate()));
        }
        return Map.of();
    }

    private long extractSpuTotal(Map<String, Aggregate> aggs, long fallback) {
        Aggregate agg = aggs.get("spu_total_agg");
        return agg != null && agg.isCardinality() ? agg.cardinality().value() : fallback;
    }

    private SearchAggregations toAggregations(Map<String, Aggregate> aggs) {
        List<BrandAggItem> brands = new ArrayList<>();
        Aggregate brandAgg = aggs.get("brand_agg");
        if (brandAgg != null && brandAgg.isLterms()) {
            for (LongTermsBucket bucket : brandAgg.lterms().buckets().array()) {
                String name = firstStringKey(bucket.aggregations().get("brand_name"));
                brands.add(new BrandAggItem(bucket.key(), name, bucket.docCount()));
            }
        }

        List<AttrAggItem> attrs = new ArrayList<>();
        Aggregate attrAgg = aggs.get("attr_agg");
        if (attrAgg != null && attrAgg.isNested()) {
            Aggregate byAttrKey = attrAgg.nested().aggregations().get("by_attr_key");
            if (byAttrKey != null && byAttrKey.isSterms()) {
                for (StringTermsBucket bucket : byAttrKey.sterms().buckets().array()) {
                    String attrName = firstStringKey(bucket.aggregations().get("attr_name"));
                    List<AttrValueCount> values = new ArrayList<>();
                    Aggregate byValue = bucket.aggregations().get("by_value");
                    if (byValue != null && byValue.isSterms()) {
                        for (StringTermsBucket vb : byValue.sterms().buckets().array()) {
                            values.add(new AttrValueCount(vb.key().stringValue(), vb.docCount()));
                        }
                    }
                    attrs.add(new AttrAggItem(bucket.key().stringValue(), attrName, values));
                }
            }
        }

        return new SearchAggregations(brands, attrs);
    }

    private String firstStringKey(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isSterms()) {
            return null;
        }
        var buckets = aggregate.sterms().buckets().array();
        return buckets.isEmpty() ? null : buckets.get(0).key().stringValue();
    }
}
