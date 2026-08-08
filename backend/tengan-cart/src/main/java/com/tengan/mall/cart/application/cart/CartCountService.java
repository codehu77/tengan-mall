package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

/**
 * 不查 tengan-product——購物車數量徽章是高頻讀取（幾乎每頁都要顯示），不該為了徽章觸發跨服務呼叫。
 * 回傳的是「幾樣商品」（行數），不是加總買了幾件——使用者明確要求，跟 momo 那種加總件數的慣例不同。
 */
@Service
public class CartCountService implements CartCountUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public CartCountService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public int count(CartOwner owner) {
        return switch (owner) {
            case CartOwner.Member member -> cartItemRepository.countLinesByUserId(member.userId());
            case CartOwner.Guest guest -> guestCartPort.findAll(guest.guestKey()).size();
        };
    }
}
