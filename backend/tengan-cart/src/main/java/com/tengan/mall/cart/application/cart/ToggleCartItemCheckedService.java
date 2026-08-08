package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.exception.CartItemNotFoundException;
import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ToggleCartItemCheckedService implements ToggleCartItemCheckedUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public ToggleCartItemCheckedService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public void toggle(CartOwner owner, Long itemId, boolean checked) {
        switch (owner) {
            case CartOwner.Member member -> {
                var item = cartItemRepository.findById(itemId)
                        .filter(i -> i.belongsTo(member.userId()))
                        .orElseThrow(() -> new CartItemNotFoundException(itemId));
                item.setChecked(checked);
                cartItemRepository.save(item);
            }
            case CartOwner.Guest guest -> {
                var existing = guestCartPort.find(guest.guestKey(), itemId)
                        .orElseThrow(() -> new CartItemNotFoundException(itemId));
                guestCartPort.save(guest.guestKey(),
                        new GuestCartItem(existing.skuId(), existing.count(), checked, existing.specText()));
            }
        }
    }
}
