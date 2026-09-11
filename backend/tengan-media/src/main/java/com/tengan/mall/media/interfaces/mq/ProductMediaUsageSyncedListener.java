package com.tengan.mall.media.interfaces.mq;

import com.tengan.mall.media.application.lifecycle.SyncOwnerMediaAssetsUseCase;
import com.tengan.mall.media.infrastructure.mq.ProductMediaUsageSyncedEvent;
import com.tengan.mall.media.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** 訂閱 tengan-product 發布的 product.media-usage.synced 事件，維護 media_asset 的 CONFIRMED/PENDING 狀態。 */
@Component
public class ProductMediaUsageSyncedListener {

    private final SyncOwnerMediaAssetsUseCase syncOwnerMediaAssetsUseCase;

    public ProductMediaUsageSyncedListener(SyncOwnerMediaAssetsUseCase syncOwnerMediaAssetsUseCase) {
        this.syncOwnerMediaAssetsUseCase = syncOwnerMediaAssetsUseCase;
    }

    @RabbitListener(queues = RabbitConfig.PRODUCT_MEDIA_USAGE_SYNCED_QUEUE)
    public void onMediaUsageSynced(ProductMediaUsageSyncedEvent event) {
        syncOwnerMediaAssetsUseCase.sync(event.ownerType(), event.ownerId(), event.urls());
    }
}
