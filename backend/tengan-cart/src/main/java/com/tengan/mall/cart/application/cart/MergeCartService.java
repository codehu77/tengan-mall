package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.model.CartItem;
import com.tengan.mall.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;

/**
 * 登入成功後前端呼叫一次：讀出訪客購物車、逐筆 upsert 進會員購物車（同 skuId 數量相加，不比價，
 * 因為兩邊都不存價格快照），完成後刪除訪客 Redis key（見 cart_storage_decision）。清 cookie 是
 * interfaces 層的責任，不在這裡處理。
 */
@Service
public class MergeCartService implements MergeCartUseCase {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;

    public MergeCartService(CartItemRepository cartItemRepository, GuestCartPort guestCartPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
    }

    @Override
    public void merge(Long userId, String guestKey) {
        if (guestKey == null) {
            return;
        }
        for (GuestCartItem guestItem : guestCartPort.findAll(guestKey)) {
            CartItem item = cartItemRepository.findByUserIdAndSkuId(userId, guestItem.skuId())
                    .map(existing -> {
                        existing.increaseCount(guestItem.count());
                        return existing;
                    })
                    .orElseGet(() -> CartItem.create(userId, guestItem.skuId(), guestItem.count(),
                            guestItem.specText()));
            cartItemRepository.save(item);
        }
        guestCartPort.deleteAll(guestKey);
    }
}
