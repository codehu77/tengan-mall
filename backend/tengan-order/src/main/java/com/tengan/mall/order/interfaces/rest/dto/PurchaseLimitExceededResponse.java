package com.tengan.mall.order.interfaces.rest.dto;

import java.util.List;

public record PurchaseLimitExceededResponse(String message, List<Long> skuIds) {
}
