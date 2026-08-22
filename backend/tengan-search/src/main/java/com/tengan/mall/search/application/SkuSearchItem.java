package com.tengan.mall.search.application;

public record SkuSearchItem(Long skuId, Long spuId, String skuName, String spuName, Double price, String mainImage,
        int saleCount, Long brandId, String brandName) {
}
