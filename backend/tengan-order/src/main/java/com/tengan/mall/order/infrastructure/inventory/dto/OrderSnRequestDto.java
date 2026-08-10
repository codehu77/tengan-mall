package com.tengan.mall.order.infrastructure.inventory.dto;

/** release/consume/revert 等等共用同一種 request 形狀，比照下游服務的既有慣例。 */
public record OrderSnRequestDto(String orderSn) {
}
