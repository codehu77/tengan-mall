package com.tengan.mall.cart.application.cart;

public interface RemoveCartItemUseCase {

    void remove(CartOwner owner, Long itemId);
}
