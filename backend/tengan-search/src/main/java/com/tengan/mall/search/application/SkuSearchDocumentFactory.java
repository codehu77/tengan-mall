package com.tengan.mall.search.application;

public final class SkuSearchDocumentFactory {

    private SkuSearchDocumentFactory() {
    }

    public static SkuSearchDocument from(ProductCatalogSkuItem p) {
        var attrs = p.attrs() == null ? null
                : p.attrs().stream()
                        .map(a -> new SkuSearchAttrValue(a.attrType() + "-" + a.attrId(), a.attrId(), a.attrName(),
                                a.attrValue()))
                        .toList();
        Double price = p.price() == null ? null : p.price().doubleValue();
        return new SkuSearchDocument(p.skuId(), p.spuId(), p.skuName(), p.spuName(), price, p.mainImage(),
                p.saleCount(), p.brandId(), p.brandName(), p.catalog1Id(), p.catalog1Name(), p.catalog2Id(),
                p.catalog2Name(), p.catalog3Id(), p.catalog3Name(), attrs);
    }
}
