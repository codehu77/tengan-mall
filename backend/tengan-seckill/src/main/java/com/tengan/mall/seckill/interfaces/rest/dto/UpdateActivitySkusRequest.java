package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateActivitySkusRequest(@NotEmpty @Valid List<SkuItemRequest> items) {
}
