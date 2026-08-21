package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BatchStatusRequest(@NotEmpty List<Long> skuIds) {
}
