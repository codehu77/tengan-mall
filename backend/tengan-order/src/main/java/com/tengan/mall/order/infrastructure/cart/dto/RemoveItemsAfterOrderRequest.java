package com.tengan.mall.order.infrastructure.cart.dto;

import java.util.List;

public record RemoveItemsAfterOrderRequest(List<Long> skuIds) {
}
