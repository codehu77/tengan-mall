package com.tengan.mall.search.interfaces.rest.dto;

public record SearchItemResponse(Long skuId, Long spuId, String skuName, Double price, String mainImage,
        int saleCount, Long brandId, String brandName) {
}
