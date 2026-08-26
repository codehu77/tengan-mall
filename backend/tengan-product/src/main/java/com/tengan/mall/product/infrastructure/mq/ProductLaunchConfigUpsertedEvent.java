package com.tengan.mall.product.infrastructure.mq;

import com.tengan.mall.product.application.spu.SkuLaunchConfigPayload;
import java.util.List;

public record ProductLaunchConfigUpsertedEvent(Long spuId, List<SkuLaunchConfigPayload> skus) {
}
