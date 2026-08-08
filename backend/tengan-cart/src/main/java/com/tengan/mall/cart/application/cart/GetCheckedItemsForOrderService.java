package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.repository.CartItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetCheckedItemsForOrderService implements GetCheckedItemsForOrderUseCase {

    private final CartItemRepository cartItemRepository;

    public GetCheckedItemsForOrderService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public List<CheckedItemView> get(Long userId) {
        return cartItemRepository.findCheckedByUserId(userId).stream()
                .map(item -> new CheckedItemView(item.getSkuId(), item.getCount()))
                .toList();
    }
}
