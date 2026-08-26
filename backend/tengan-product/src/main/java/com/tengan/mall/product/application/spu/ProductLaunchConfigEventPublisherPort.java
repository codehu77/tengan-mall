package com.tengan.mall.product.application.spu;

import java.util.List;

/**
 * 呼叫端命名（不是 RabbitPublisherPort）——真正的技術實作（RabbitMQ）在 infrastructure 層。
 * tengan-inventory 消費這個事件，在本地維護一份 sku_launch_config 副本，用來做開賣時間閘門/
 * 限購檢查（Phase A 只做 MySQL 條件式 UPDATE，不含 Redis 流量閘門）。跟 ProductSearchEventPublisherPort
 * 不同：這裡不管 SPU 是不是 ON_SHELF，只要存檔就發——這是內部設定同步，不是公開可見性判斷。
 */
public interface ProductLaunchConfigEventPublisherPort {

    void publishUpserted(Long spuId, List<SkuLaunchConfigPayload> skus);

    /**
     * UpdateSpuService 整批替換 SKU 語意下，舊 skuId 會被刪除換上全新 id 的新列（見該類別說明）——
     * 舊 skuId 對應的 sku_launch_config 副本列也要跟著清掉，不然 tengan-inventory 那邊會一直
     * 累積永遠查不到對應商品的孤兒列，每次編輯商品都多留一批（跟 ProductSearchEventPublisherPort
     * 同樣的坑，這裡要比照處理）。
     */
    void publishRemoved(Long spuId, List<Long> skuIds);
}
