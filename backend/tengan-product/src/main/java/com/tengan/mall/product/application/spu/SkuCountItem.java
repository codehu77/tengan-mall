package com.tengan.mall.product.application.spu;

/** order.completed 事件裡一筆明細，轉成 application 層看得懂的形狀（跟 infra 層的 MQ DTO 分開）。 */
public record SkuCountItem(Long skuId, Long spuId, int count) {
}
