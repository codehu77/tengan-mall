package com.tengan.mall.product.infrastructure.mq;

import java.util.List;

public record ProductRemovedEvent(Long spuId, List<Long> skuIds) {
}
