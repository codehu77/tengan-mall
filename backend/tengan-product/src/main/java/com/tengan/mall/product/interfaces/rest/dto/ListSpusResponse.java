package com.tengan.mall.product.interfaces.rest.dto;

import java.util.List;

public record ListSpusResponse(List<SpuSummaryResponse> items, long total) {
}
