package com.tengan.mall.order.infrastructure.seckill.dto;

import java.util.List;

public record BatchStatusResponseDto(List<ActiveSkuDto> activeSkus) {
}
