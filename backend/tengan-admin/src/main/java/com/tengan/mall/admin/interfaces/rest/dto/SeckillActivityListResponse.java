package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record SeckillActivityListResponse(List<SeckillActivityResponse> items, long total) {
}
