package com.tengan.mall.seckill.infrastructure.mq;

import java.util.List;

/** 跟 tengan-product 發布端（product-search-exchange / product.removed）的 JSON 形狀對齊，不共用型別。 */
public record ProductRemovedEvent(Long spuId, List<Long> skuIds) {
}
