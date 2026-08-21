package com.tengan.mall.order.infrastructure.seckill.dto;

/** tengan-seckill 錯誤回應的形狀（{@code Map.of("message", ...)} 序列化後）。 */
public record SeckillErrorDto(String message) {
}
