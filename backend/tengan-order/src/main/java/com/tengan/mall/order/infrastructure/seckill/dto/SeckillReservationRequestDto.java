package com.tengan.mall.order.infrastructure.seckill.dto;

public record SeckillReservationRequestDto(Long skuId, Long memberId, int count) {
}
