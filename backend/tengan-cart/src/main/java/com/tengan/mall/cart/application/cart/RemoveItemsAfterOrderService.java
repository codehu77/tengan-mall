package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.repository.CartItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RemoveItemsAfterOrderService implements RemoveItemsAfterOrderUseCase {

    private final CartItemRepository cartItemRepository;

    public RemoveItemsAfterOrderService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void remove(Long userId, List<Long> skuIds) {
        cartItemRepository.deleteByUserIdAndSkuIdIn(userId, skuIds);
    }
}
