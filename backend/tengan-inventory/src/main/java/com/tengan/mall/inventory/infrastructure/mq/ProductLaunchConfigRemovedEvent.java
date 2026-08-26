package com.tengan.mall.inventory.infrastructure.mq;

import java.util.List;

/** 跟 tengan-product 發布端（product-launch-config-exchange / product.launch-config.removed）的 JSON 形狀對齊，不共用型別。 */
public record ProductLaunchConfigRemovedEvent(Long spuId, List<Long> skuIds) {
}
