package com.tengan.mall.cart.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartListResponse(List<CartLineResponse> items, BigDecimal checkedTotalPrice, int totalItemCount) {
}
