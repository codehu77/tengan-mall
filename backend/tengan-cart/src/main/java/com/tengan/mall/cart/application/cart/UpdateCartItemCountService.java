package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.exception.CartItemNotFoundException;
import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

/**
 * itemId 對會員是 cart_item 的 db id、對訪客是 skuId 本身——見 CartLineView 的註解，兩種身份的
 * 呼叫端拿到的都是同一個欄位，不需要知道底層儲存差異。
 */
@Service
public class UpdateCartItemCountService implements UpdateCartItemCountUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public UpdateCartItemCountService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public void update(CartOwner owner, Long itemId, int count) {
        switch (owner) {
            case CartOwner.Member member -> {
                var item = cartItemRepository.findById(itemId)
                        .filter(i -> i.belongsTo(member.userId()))
                        .orElseThrow(() -> new CartItemNotFoundException(itemId));
                item.changeCount(count);
                cartItemRepository.save(item);
            }
            case CartOwner.Guest guest -> {
                var existing = guestCartPort.find(guest.guestKey(), itemId)
                        .orElseThrow(() -> new CartItemNotFoundException(itemId));
                guestCartPort.save(guest.guestKey(),
                        new GuestCartItem(existing.skuId(), count, existing.checked(), existing.specText()));
            }
        }
    }
}
