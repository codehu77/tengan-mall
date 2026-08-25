package com.tengan.mall.product.infrastructure.mq;

/** tengan-order 的 order.completed 事件裡一筆明細——只取這個服務用得到的欄位，多出來的欄位
 * （skuName/price/subtotal...）Jackson 反序列化時直接忽略，不用整份對齊生產端的 DTO。 */
public record OrderItemEvent(Long skuId, Long spuId, int count) {
}
