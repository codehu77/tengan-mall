package com.tengan.mall.cart.application.cart;

public record AddCartItemCommand(CartOwner owner, Long skuId, int count, String specText) {
}
