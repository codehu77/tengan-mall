package com.tengan.mall.cart.application.cart;

public interface UpdateCartItemCountUseCase {

    void update(CartOwner owner, Long itemId, int count);
}
