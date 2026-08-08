package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.model.CartItem;
import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

/**
 * 同一個 sku 已在購物車時是「數量相加」，不是覆蓋（比照大部分電商的加入購物車行為）——
 * 會員/訪客各自沿用 (user_id,sku_id) 唯一鍵/skuId 當 Redis hash field 天生保證的「一個 sku 一行」
 * 不變條件，見 cart_storage_decision。
 */
@Service
public class AddCartItemService implements AddCartItemUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public AddCartItemService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public AddCartItemResult add(AddCartItemCommand command) {
        return switch (command.owner()) {
            case CartOwner.Member member -> addForMember(member.userId(), command);
            case CartOwner.Guest guest -> addForGuest(guest.guestKey(), command);
        };
    }

    private AddCartItemResult addForMember(Long userId, AddCartItemCommand command) {
        CartItem item = cartItemRepository.findByUserIdAndSkuId(userId, command.skuId())
                .map(existing -> {
                    existing.increaseCount(command.count());
                    if (command.specText() != null) {
                        existing.updateSpecText(command.specText());
                    }
                    return existing;
                })
                .orElseGet(() -> CartItem.create(userId, command.skuId(), command.count(), command.specText()));
        cartItemRepository.save(item);
        return new AddCartItemResult(item.getId());
    }

    private AddCartItemResult addForGuest(String guestKey, AddCartItemCommand command) {
        var existing = guestCartPort.find(guestKey, command.skuId());
        int newCount = existing.map(GuestCartItem::count).orElse(0) + command.count();
        String specText = command.specText() != null ? command.specText()
                : existing.map(GuestCartItem::specText).orElse(null);
        guestCartPort.save(guestKey, new GuestCartItem(command.skuId(), newCount, true, specText));
        return new AddCartItemResult(command.skuId());
    }
}
