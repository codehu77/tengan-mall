package com.tengan.mall.search.interfaces.rest.dto;

public record SearchItemResponse(Long spuId, String spuName, Double minPrice, Double maxPrice, String mainImage,
        int saleCount, Long brandId, String brandName, Long catalog1Id) {
}
