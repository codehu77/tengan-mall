package com.tengan.mall.search.application;

import java.util.List;

public final class SpuSearchDocumentFactory {

    private SpuSearchDocumentFactory() {
    }

    public static SpuSearchDocument from(ProductCatalogSpuItem p) {
        var baseAttrs = toAttrValues(p.baseAttrs());
        var skus = p.skus() == null ? null
                : p.skus().stream()
                        .map(v -> new SkuVariant(v.skuId(), v.skuName(),
                                v.price() == null ? null : v.price().doubleValue(), v.mainImage(), v.saleCount(),
                                toAttrValues(v.saleAttrs())))
                        .toList();
        Double minPrice = p.minPrice() == null ? null : p.minPrice().doubleValue();
        Double maxPrice = p.maxPrice() == null ? null : p.maxPrice().doubleValue();
        return new SpuSearchDocument(p.spuId(), p.spuName(), p.spuMainImage(), minPrice, maxPrice, p.saleCount(),
                p.brandId(), p.brandName(), p.catalog1Id(), p.catalog1Name(), p.catalog2Id(), p.catalog2Name(),
                p.catalog3Id(), p.catalog3Name(), baseAttrs, skus);
    }

    private static List<SkuSearchAttrValue> toAttrValues(List<ProductCatalogAttrItem> attrs) {
        return attrs == null ? null
                : attrs.stream()
                        .map(a -> new SkuSearchAttrValue(a.attrType() + "-" + a.attrId(), a.attrId(), a.attrName(),
                                a.attrValue()))
                        .toList();
    }
}
