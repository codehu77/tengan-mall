package com.tengan.mall.inventory.domain.repository;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SkuLaunchConfigRepository {

    Optional<SkuLaunchConfig> findBySkuId(Long skuId);

    /** 訂閱 tengan-product 的 product.launch-config.upserted 事件用，skuId 是全域唯一 PK，直接覆蓋既有值。 */
    void upsert(Long skuId, LocalDateTime saleStartTime, Integer purchaseLimitPerUser);
}
