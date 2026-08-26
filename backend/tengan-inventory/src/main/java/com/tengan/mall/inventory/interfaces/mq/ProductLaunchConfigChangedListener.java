package com.tengan.mall.inventory.interfaces.mq;

import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.infrastructure.mq.ProductLaunchConfigChangedEvent;
import com.tengan.mall.inventory.infrastructure.mq.ProductLaunchConfigRemovedEvent;
import com.tengan.mall.inventory.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 訂閱 tengan-product 發布的 product.launch-config.upserted/removed 事件，維護本地的
 * sku_launch_config 副本。UpdateSpuService 整批替換 SKU 語意下，舊 skuId 會被刪除換上全新 id
 * 的新列，所以「upserted 帶目前完整清單」跟「removed 帶被替換掉的舊 skuId」這兩個事件都要處理，
 * 不然舊 skuId 的副本列會一直留著查不到對應商品，永久累積孤兒列（每編輯一次商品就多留一批）。
 */
@Component
public class ProductLaunchConfigChangedListener {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;

    public ProductLaunchConfigChangedListener(SkuLaunchConfigRepository skuLaunchConfigRepository) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
    }

    @RabbitListener(queues = RabbitConfig.PRODUCT_LAUNCH_CONFIG_QUEUE)
    public void onLaunchConfigChanged(ProductLaunchConfigChangedEvent event) {
        for (var sku : event.skus()) {
            skuLaunchConfigRepository.upsert(sku.skuId(), sku.saleStartTime(), sku.purchaseLimitPerUser());
        }
    }

    @RabbitListener(queues = RabbitConfig.PRODUCT_LAUNCH_CONFIG_REMOVED_QUEUE)
    public void onLaunchConfigRemoved(ProductLaunchConfigRemovedEvent event) {
        if (!event.skuIds().isEmpty()) {
            skuLaunchConfigRepository.deleteBySkuIds(event.skuIds());
        }
    }
}
