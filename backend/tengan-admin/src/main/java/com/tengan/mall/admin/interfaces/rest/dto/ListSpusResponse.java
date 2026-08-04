package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record ListSpusResponse(List<SpuSummaryResponse> items, long total) {
}
