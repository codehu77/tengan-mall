package com.tengan.mall.search.infrastructure.mq;

public record SkuAttrPayload(Long attrId, String attrType, String attrName, String attrValue) {
}
