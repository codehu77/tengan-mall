package com.tengan.mall.cart.domain.exception;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long id) {
        super("購物車項目不存在: " + id);
    }
}
