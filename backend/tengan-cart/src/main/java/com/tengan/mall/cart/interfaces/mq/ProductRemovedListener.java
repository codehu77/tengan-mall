package com.tengan.mall.cart.interfaces.mq;

import com.tengan.mall.cart.domain.repository.CartItemRepository;
import com.tengan.mall.cart.infrastructure.mq.ProductRemovedEvent;
import com.tengan.mall.cart.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 訂閱 tengan-product 發布的 product.removed 事件，把失效 skuId 對應的購物車列清掉——不清的話，
 * 商品規格真的被刪除後，購物車那筆項目會一直留著顯示成失效的空白列，沒有任何機制自動移除。
 * 只處理會員購物車（cart_item 是「永久保留」的那份），訪客購物車走 Redis TTL 會自然過期。
 */
@Component
public class ProductRemovedListener {

    private final CartItemRepository cartItemRepository;

    public ProductRemovedListener(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @RabbitListener(queues = RabbitConfig.PRODUCT_REMOVED_QUEUE)
    public void onProductRemoved(ProductRemovedEvent event) {
        if (!event.skuIds().isEmpty()) {
            cartItemRepository.deleteBySkuIds(event.skuIds());
        }
    }
}
