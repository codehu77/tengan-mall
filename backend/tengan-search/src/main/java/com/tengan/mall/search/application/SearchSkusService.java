package com.tengan.mall.search.application;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import java.util.ArrayList;
import java.util.Comparator;
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
 *
 * <p>索引單位是 SPU（一份文件一個商品），不需要 field collapse 摺疊代表值——minPrice/maxPrice
 * 是索引階段就算好的彙總欄位，一份文件本來就對應一個 SPU，hits.getTotalHits() 直接就是符合條件的
 * SPU 數，不用再另外用 cardinality aggregation 算。</p>
 *
 * <p>屬性篩選分兩種：BASE 屬性是 SPU 層級（單層 nested，邏輯跟改版前一樣），SALE 屬性是規格層級，
 * 掛在 skus 底下（nested-in-nested）——多個 SALE 條件必須合併進「同一個」nested(path="skus")
 * 查詢裡一起判斷，不能各自包成獨立的 filter，否則會發生「顏色=A 的規格」跟「容量=B 的規格」各自
 * 存在、卻被誤判成「同一顆規格同時是 A 顏色又是 B 容量」的組合。</p>
 */
@Service
public class SearchSkusService implements SearchSkusUseCase {

    private static final int BRAND_AGG_SIZE = 30;
    private static final int ATTR_ID_AGG_SIZE = 30;
    private static final int ATTR_VALUE_AGG_SIZE = 30;
    private static final String SALE_ATTR_PREFIX = "SALE-";

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

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(esQuery)
                .withPageable(PageRequest.of(Math.max(query.page() - 1, 0), query.pageSize(), buildSort(query)))
                .withAggregation("brand_agg", buildBrandAggregation())
                .withAggregation("base_attr_agg", buildBaseAttrAggregation())
                .withAggregation("sale_attr_agg", buildSaleAttrAggregation())
                .build();

        SearchHits<SpuSearchDocument> hits = elasticsearchOperations.search(nativeQuery, SpuSearchDocument.class);

        List<SpuSearchItem> items = hits.getSearchHits().stream().map(this::toItem).toList();
        Map<String, Aggregate> aggs = extractAggregates(hits);
        SearchAggregations aggregations = toAggregations(aggs);

        return new SearchSkusResult(items, hits.getTotalHits(), query.page(), query.pageSize(), aggregations);
    }

    private Query buildQuery(SearchSkusQuery query) {
        BoolQuery.Builder bool = new BoolQuery.Builder();

        if (query.keyword() != null && !query.keyword().isBlank()) {
            // 商品名、任一規格名、規格屬性的原始行銷值命中都算——spuName 頂層欄位，skuName 掛在 nested
            // 的 skus 底下，baseAttrs.attrValue 單層 nested，skus.saleAttrs.attrValue 雙層 nested
            // （比照下面 attrs filter 迴圈已經在用的雙層 nested 包法）。原始行銷值（例如「鼠尾草綠色」）
            // 即使已經綁定標準聚合值，關鍵字全文搜尋還是要比對得到，不能只靠正規化後的 facetValue。
            bool.must(m -> m.bool(b -> b
                    .should(s -> s.multiMatch(mm -> mm.query(query.keyword()).fields("spuName")))
                    .should(s -> s.nested(n -> n.path("skus")
                            .query(nq -> nq.multiMatch(mm -> mm.query(query.keyword()).fields("skus.skuName")))))
                    .should(s -> s.nested(n -> n.path("baseAttrs").query(
                            nq -> nq.multiMatch(mm -> mm.query(query.keyword()).fields("baseAttrs.attrValue")))))
                    .should(s -> s.nested(n -> n.path("skus").query(nq -> nq.nested(n2 -> n2.path("skus.saleAttrs")
                            .query(nq2 -> nq2.multiMatch(
                                    mm -> mm.query(query.keyword()).fields("skus.saleAttrs.attrValue")))))))
                    .minimumShouldMatch("1")));
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
            List<Query> saleAttrConditions = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : query.attrs().entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    continue;
                }
                String attrKey = entry.getKey();
                List<FieldValue> values = entry.getValue().stream().map(FieldValue::of).toList();
                if (attrKey.startsWith(SALE_ATTR_PREFIX)) {
                    saleAttrConditions.add(Query.of(q -> q.nested(n -> n.path("skus.saleAttrs")
                            .query(nq -> nq.bool(nb -> nb
                                    .must(nm -> nm.term(t -> t.field("skus.saleAttrs.attrKey").value(attrKey)))
                                    .must(nm -> nm.terms(
                                            t -> t.field("skus.saleAttrs.facetValue").terms(tt -> tt.value(values))))))
                    )));
                } else {
                    bool.filter(f -> f.nested(n -> n.path("baseAttrs")
                            .query(nq -> nq.bool(nb -> nb
                                    .must(nm -> nm.term(t -> t.field("baseAttrs.attrKey").value(attrKey)))
                                    .must(nm -> nm.terms(
                                            t -> t.field("baseAttrs.facetValue").terms(tt -> tt.value(values))))))));
                }
            }
            // 所有 SALE 條件包進「同一個」nested(path="skus")，內層再各自 nested(path="skus.saleAttrs")——
            // 這樣 ES 才會在「同一顆規格」的範圍內同時檢查 must 的每一個條件，而不是分別在不同規格上各自成立。
            if (!saleAttrConditions.isEmpty()) {
                BoolQuery.Builder skusInnerBool = new BoolQuery.Builder();
                saleAttrConditions.forEach(skusInnerBool::must);
                BoolQuery innerBool = skusInnerBool.build();
                bool.filter(f -> f.nested(n -> n.path("skus").query(nq -> nq.bool(innerBool))));
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
            // 排序依據跟卡片上顯示的數字保持一致——起價就是 minPrice，asc/desc 都排這個欄位。
            case "price" -> "minPrice";
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

    private Aggregation buildBaseAttrAggregation() {
        Aggregation facetSort = Aggregation.of(a -> a.min(m -> m.field("baseAttrs.facetSort")));
        Aggregation byValue = Aggregation.of(a -> a
                .terms(t -> t.field("baseAttrs.facetValue").size(ATTR_VALUE_AGG_SIZE))
                .aggregations("facet_sort", facetSort));
        Aggregation attrName = Aggregation.of(a -> a.terms(t -> t.field("baseAttrs.attrName").size(1)));
        Aggregation byAttrKey = Aggregation.of(a -> a
                .terms(t -> t.field("baseAttrs.attrKey").size(ATTR_ID_AGG_SIZE))
                .aggregations("attr_name", attrName)
                .aggregations("by_value", byValue));
        return Aggregation.of(a -> a
                .nested(n -> n.path("baseAttrs"))
                .aggregations("by_attr_key", byAttrKey));
    }

    /**
     * SALE 屬性掛在 nested 的 skus 底下、saleAttrs 又是 nested-in-nested，terms 聚合預設的
     * docCount 是「符合條件的 nested sku/saleAttr 子文件數」——同一個 SPU 可能有兩顆規格都是黑色，
     * 不處理的話「黑色 (2)」會誤導成兩件商品。用 reverse_nested（不指定 path，直接跳回根文件層級）
     * 把計數還原成「符合條件的 SPU 數」。
     */
    private Aggregation buildSaleAttrAggregation() {
        Aggregation spuCount = Aggregation.of(a -> a.reverseNested(rn -> rn));
        Aggregation facetSort = Aggregation.of(a -> a.min(m -> m.field("skus.saleAttrs.facetSort")));
        Aggregation byValue = Aggregation.of(a -> a
                .terms(t -> t.field("skus.saleAttrs.facetValue").size(ATTR_VALUE_AGG_SIZE))
                .aggregations("spu_count", spuCount)
                .aggregations("facet_sort", facetSort));
        Aggregation attrName = Aggregation.of(a -> a.terms(t -> t.field("skus.saleAttrs.attrName").size(1)));
        Aggregation byAttrKey = Aggregation.of(a -> a
                .terms(t -> t.field("skus.saleAttrs.attrKey").size(ATTR_ID_AGG_SIZE))
                .aggregations("attr_name", attrName)
                .aggregations("by_value", byValue));
        Aggregation toSaleAttrs = Aggregation.of(a -> a
                .nested(n -> n.path("skus.saleAttrs"))
                .aggregations("by_attr_key", byAttrKey));
        return Aggregation.of(a -> a
                .nested(n -> n.path("skus"))
                .aggregations("to_sale_attrs", toSaleAttrs));
    }

    private SpuSearchItem toItem(SearchHit<SpuSearchDocument> hit) {
        SpuSearchDocument d = hit.getContent();
        return new SpuSearchItem(d.getSpuId(), d.getSpuName(), d.getMinPrice(), d.getMaxPrice(), d.getSpuMainImage(),
                d.getSaleCount() == null ? 0 : d.getSaleCount(), d.getBrandId(), d.getBrandName(),
                d.getCatalog1Id());
    }

    private Map<String, Aggregate> extractAggregates(SearchHits<SpuSearchDocument> hits) {
        AggregationsContainer<?> container = hits.getAggregations();
        if (container instanceof ElasticsearchAggregations esAggs) {
            return esAggs.aggregations().stream()
                    .collect(java.util.stream.Collectors.toMap(a -> a.aggregation().getName(),
                            a -> a.aggregation().getAggregate()));
        }
        return Map.of();
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
        attrs.addAll(extractBaseAttrAggs(aggs.get("base_attr_agg")));
        attrs.addAll(extractSaleAttrAggs(aggs.get("sale_attr_agg")));

        return new SearchAggregations(brands, attrs);
    }

    private List<AttrAggItem> extractBaseAttrAggs(Aggregate baseAttrAgg) {
        List<AttrAggItem> result = new ArrayList<>();
        if (baseAttrAgg == null || !baseAttrAgg.isNested()) {
            return result;
        }
        Aggregate byAttrKey = baseAttrAgg.nested().aggregations().get("by_attr_key");
        if (byAttrKey == null || !byAttrKey.isSterms()) {
            return result;
        }
        for (StringTermsBucket bucket : byAttrKey.sterms().buckets().array()) {
            String attrName = firstStringKey(bucket.aggregations().get("attr_name"));
            List<AttrValueCount> values = new ArrayList<>();
            Aggregate byValue = bucket.aggregations().get("by_value");
            if (byValue != null && byValue.isSterms()) {
                List<StringTermsBucket> valueBuckets = new ArrayList<>(byValue.sterms().buckets().array());
                valueBuckets.sort(Comparator.comparingDouble(this::facetSortOf));
                for (StringTermsBucket vb : valueBuckets) {
                    values.add(new AttrValueCount(vb.key().stringValue(), vb.docCount()));
                }
            }
            result.add(new AttrAggItem(bucket.key().stringValue(), attrName, values));
        }
        return result;
    }

    private List<AttrAggItem> extractSaleAttrAggs(Aggregate saleAttrAgg) {
        List<AttrAggItem> result = new ArrayList<>();
        if (saleAttrAgg == null || !saleAttrAgg.isNested()) {
            return result;
        }
        Aggregate toSaleAttrs = saleAttrAgg.nested().aggregations().get("to_sale_attrs");
        if (toSaleAttrs == null || !toSaleAttrs.isNested()) {
            return result;
        }
        Aggregate byAttrKey = toSaleAttrs.nested().aggregations().get("by_attr_key");
        if (byAttrKey == null || !byAttrKey.isSterms()) {
            return result;
        }
        for (StringTermsBucket bucket : byAttrKey.sterms().buckets().array()) {
            String attrName = firstStringKey(bucket.aggregations().get("attr_name"));
            List<AttrValueCount> values = new ArrayList<>();
            Aggregate byValue = bucket.aggregations().get("by_value");
            if (byValue != null && byValue.isSterms()) {
                List<StringTermsBucket> valueBuckets = new ArrayList<>(byValue.sterms().buckets().array());
                valueBuckets.sort(Comparator.comparingDouble(this::facetSortOf));
                for (StringTermsBucket vb : valueBuckets) {
                    Aggregate spuCount = vb.aggregations().get("spu_count");
                    long count = spuCount != null && spuCount.isReverseNested() ? spuCount.reverseNested().docCount()
                            : vb.docCount();
                    values.add(new AttrValueCount(vb.key().stringValue(), count));
                }
            }
            result.add(new AttrAggItem(bucket.key().stringValue(), attrName, values));
        }
        return result;
    }

    /** 標準聚合值照後台拖曳的排序顯示，未綁定的原始值（沒有 facet_sort 子聚合可用）固定排在最後。 */
    private double facetSortOf(StringTermsBucket bucket) {
        Aggregate facetSort = bucket.aggregations().get("facet_sort");
        return facetSort != null && facetSort.isMin() ? facetSort.min().value() : Integer.MAX_VALUE;
    }

    private String firstStringKey(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isSterms()) {
            return null;
        }
        var buckets = aggregate.sterms().buckets().array();
        return buckets.isEmpty() ? null : buckets.get(0).key().stringValue();
    }
}
