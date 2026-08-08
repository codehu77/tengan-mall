package com.tengan.mall.cart.interfaces.rest.dto;

import java.util.List;

public record RemoveItemsAfterOrderRequest(List<Long> skuIds) {
}
