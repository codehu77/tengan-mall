package com.tengan.mall.cart.application.cart;

public interface MiniCartUseCase {

    CartListResult mini(CartOwner owner, int limit);
}
