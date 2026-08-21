package com.tengan.mall.order.infrastructure.seckill.dto;

import java.util.List;

public record BatchStatusRequestDto(List<Long> skuIds) {
}
