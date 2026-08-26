package com.tengan.mall.product.infrastructure.mq;

import java.util.List;

public record ProductLaunchConfigRemovedEvent(Long spuId, List<Long> skuIds) {
}
