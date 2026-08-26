package com.tengan.mall.inventory.interfaces.mq;

import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.infrastructure.mq.ProductLaunchConfigChangedEvent;
import com.tengan.mall.inventory.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 訂閱 tengan-product 發布的 product.launch-config.upserted 事件，把整批 sku 設定 upsert 進本地的
 * sku_launch_config 副本。skuId 是全域唯一 PK，天生冪等，不用處理「sku 被移除」的情況——舊設定留著
 * 也無妨，反正對應的商品詳情頁已經 404，不會被查到。
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
}
