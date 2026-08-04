package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record SearchOperLogsResponse(List<OperLogItemResponse> items, long total) {
}
