package com.tengan.mall.product.interfaces.rest.dto;

public record SearchAttrResponse(Long attrId, String attrType, String attrName, String attrValue, String facetValue,
        int facetSort) {
}
