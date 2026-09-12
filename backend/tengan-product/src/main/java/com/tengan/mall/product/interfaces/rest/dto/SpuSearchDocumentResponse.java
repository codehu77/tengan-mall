package com.tengan.mall.product.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record SpuSearchDocumentResponse(Long spuId, String spuName, String spuMainImage, BigDecimal minPrice,
        BigDecimal maxPrice, int saleCount, Long brandId, String brandName, Long catalog1Id, String catalog1Name,
        Long catalog2Id, String catalog2Name, Long catalog3Id, String catalog3Name,
        List<SearchAttrResponse> baseAttrs, List<SkuVariantResponse> skus) {
}
