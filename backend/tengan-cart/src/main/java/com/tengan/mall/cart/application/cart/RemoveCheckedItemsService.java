package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveCheckedItemsService implements RemoveCheckedItemsUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public RemoveCheckedItemsService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public void removeChecked(CartOwner owner) {
        switch (owner) {
            case CartOwner.Member member -> cartItemRepository.deleteCheckedByUserId(member.userId());
            case CartOwner.Guest guest -> guestCartPort.removeChecked(guest.guestKey());
        }
    }
}
