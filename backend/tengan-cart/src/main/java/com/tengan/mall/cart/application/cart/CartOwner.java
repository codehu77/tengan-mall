package com.tengan.mall.cart.application.cart;

/**
 * 購物車身份：會員走 MySQL（CartItemRepository），訪客走 Redis（GuestCartPort），見
 * cart_storage_decision。由 interfaces 層的 CartIdentityFilter 依 Authorization header
 * 是否能成功解出 memberId 決定是 Member 還是 Guest，application 層的 use case 依這個型別分流。
 */
public sealed interface CartOwner {

    record Member(Long userId) implements CartOwner {
    }

    record Guest(String guestKey) implements CartOwner {
    }
}
