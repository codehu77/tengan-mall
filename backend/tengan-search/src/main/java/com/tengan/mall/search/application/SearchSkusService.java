package com.tengan.mall.search.application;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
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

    @Override
    public SearchSkusResult search(SearchSkusQuery query) {
        Query esQuery = buildQuery(query);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(esQuery)
                .withPageable(PageRequest.of(Math.max(query.page() - 1, 0), query.pageSize(), buildSort(query)))
                .withAggregation("brand_agg", buildBrandAggregation())
                .withAggregation("attr_agg", buildAttrAggregation())
                .build();

        SearchHits<SkuSearchDocument> hits = elasticsearchOperations.search(nativeQuery, SkuSearchDocument.class);

        List<SkuSearchItem> items = hits.getSearchHits().stream().map(this::toItem).toList();
        SearchAggregations aggregations = toAggregations(hits);

        return new SearchSkusResult(items, hits.getTotalHits(), query.page(), query.pageSize(), aggregations);
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
        return new SkuSearchItem(d.getSkuId(), d.getSpuId(), d.getSkuName(), d.getPrice(), d.getMainImage(),
                d.getSaleCount() == null ? 0 : d.getSaleCount(), d.getBrandId(), d.getBrandName());
    }

    private SearchAggregations toAggregations(SearchHits<SkuSearchDocument> hits) {
        AggregationsContainer<?> container = hits.getAggregations();
        Map<String, Aggregate> aggs = Map.of();
        if (container instanceof ElasticsearchAggregations esAggs) {
            aggs = esAggs.aggregations().stream()
                    .collect(java.util.stream.Collectors.toMap(a -> a.aggregation().getName(),
                            a -> a.aggregation().getAggregate()));
        }

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
