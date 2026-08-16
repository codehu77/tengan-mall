package com.tengan.mall.order.infrastructure.wallet.dto;

public record ConsumePointsRequestDto(Long memberId, int points, String orderSn) {
}
