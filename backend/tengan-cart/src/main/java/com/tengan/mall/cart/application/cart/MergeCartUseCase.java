package com.tengan.mall.cart.application.cart;

public interface MergeCartUseCase {

    /** guestKey 可能是 null（從未有過訪客購物車 cookie）——此時直接視為沒有東西要合併。 */
    void merge(Long userId, String guestKey);
}
