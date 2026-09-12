package com.tengan.mall.search.application;

public record SpuSearchItem(Long spuId, String spuName, Double minPrice, Double maxPrice, String mainImage,
        int saleCount, Long brandId, String brandName, Long catalog1Id) {
}
