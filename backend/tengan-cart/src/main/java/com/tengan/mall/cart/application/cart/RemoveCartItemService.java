package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.exception.CartItemNotFoundException;
import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveCartItemService implements RemoveCartItemUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public RemoveCartItemService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public void remove(CartOwner owner, Long itemId) {
        switch (owner) {
            case CartOwner.Member member -> {
                var item = cartItemRepository.findById(itemId)
                        .filter(i -> i.belongsTo(member.userId()))
                        .orElseThrow(() -> new CartItemNotFoundException(itemId));
                cartItemRepository.deleteById(item.getId());
            }
            case CartOwner.Guest guest -> guestCartPort.remove(guest.guestKey(), itemId);
        }
    }
}
