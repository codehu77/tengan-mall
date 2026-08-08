package com.tengan.mall.cart.application.cart;

public interface ToggleCartItemCheckedUseCase {

    void toggle(CartOwner owner, Long itemId, boolean checked);
}
