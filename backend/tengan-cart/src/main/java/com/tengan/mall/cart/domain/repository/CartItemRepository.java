package com.tengan.mall.cart.domain.repository;

import com.tengan.mall.cart.domain.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository {

    CartItem save(CartItem item);

    Optional<CartItem> findById(Long id);

    Optional<CartItem> findByUserIdAndSkuId(Long userId, Long skuId);

    List<CartItem> findByUserId(Long userId);

    List<CartItem> findCheckedByUserId(Long userId);

    /** 購物車數量徽章用——是清單行數（幾樣商品），不是加總 count（買了幾件），使用者明確要求後者不直覺。 */
    int countLinesByUserId(Long userId);

    void setCheckedForUser(Long userId, boolean checked);

    void deleteById(Long id);

    void deleteCheckedByUserId(Long userId);

    /** 給 tengan-order 下單成功後用（internal 端點），依 skuId 清單移除已下單項目。 */
    void deleteByUserIdAndSkuIdIn(Long userId, List<Long> skuIds);
}
