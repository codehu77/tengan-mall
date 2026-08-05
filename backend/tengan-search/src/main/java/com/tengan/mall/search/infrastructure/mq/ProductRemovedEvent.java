package com.tengan.mall.search.infrastructure.mq;

import java.util.List;

public record ProductRemovedEvent(Long spuId, List<Long> skuIds) {
}
