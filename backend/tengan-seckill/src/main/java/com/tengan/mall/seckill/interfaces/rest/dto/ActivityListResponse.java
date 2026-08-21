package com.tengan.mall.seckill.interfaces.rest.dto;

import java.util.List;

public record ActivityListResponse(List<ActivityResponse> items, long total) {
}
