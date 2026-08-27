package com.tengan.mall.inventory.interfaces.mq;

import com.tengan.mall.inventory.application.sku.RemoveSkuTracesUseCase;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.infrastructure.mq.ProductLaunchConfigChangedEvent;
import com.tengan.mall.inventory.infrastructure.mq.ProductLaunchConfigRemovedEvent;
import com.tengan.mall.inventory.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 訂閱 tengan-product 發布的 product.launch-config.upserted/removed 事件，維護本地的
 * sku_launch_config 副本。UpdateSpuService 整批替換 SKU 語意下、DeleteSpuService 整顆刪除語意下，
 * 舊 skuId 都會消失，所以「upserted 帶目前完整清單」跟「removed 帶消失的 skuId」這兩個事件都要
 * 處理，不然舊 skuId 的副本資料會一直留著查不到對應商品，永久累積孤兒列。removed 這支不直接碰
 * repository，轉呼叫 {@link RemoveSkuTracesUseCase}——這個服務對 skuId 擁有的所有東西（不只
 * sku_launch_config）都要一次清乾淨，見該類別的說明。
 */
@Component
public class ProductLaunchConfigChangedListener {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final RemoveSkuTracesUseCase removeSkuTracesUseCase;

    public ProductLaunchConfigChangedListener(SkuLaunchConfigRepository skuLaunchConfigRepository,
            RemoveSkuTracesUseCase removeSkuTracesUseCase) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.removeSkuTracesUseCase = removeSkuTracesUseCase;
    }

    @RabbitListener(queues = RabbitConfig.PRODUCT_LAUNCH_CONFIG_QUEUE)
    public void onLaunchConfigChanged(ProductLaunchConfigChangedEvent event) {
        for (var sku : event.skus()) {
            skuLaunchConfigRepository.upsert(sku.skuId(), event.spuId(), sku.saleStartTime(),
                    sku.purchaseLimitPerUser());
        }
    }

    @RabbitListener(queues = RabbitConfig.PRODUCT_LAUNCH_CONFIG_REMOVED_QUEUE)
    public void onLaunchConfigRemoved(ProductLaunchConfigRemovedEvent event) {
        removeSkuTracesUseCase.remove(event.skuIds());
    }
}
