package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ToggleCheckedAllService implements ToggleCheckedAllUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public ToggleCheckedAllService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public void toggleAll(CartOwner owner, boolean checked) {
        switch (owner) {
            case CartOwner.Member member -> cartItemRepository.setCheckedForUser(member.userId(), checked);
            case CartOwner.Guest guest -> guestCartPort.setCheckedAll(guest.guestKey(), checked);
        }
    }
}
