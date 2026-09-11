package com.tengan.mall.media.infrastructure.mq;

import java.util.List;

/** 跟 tengan-product 發布端（product-media-usage-exchange / product.media-usage.synced）的 JSON 形狀對齊，不共用型別。 */
public record ProductMediaUsageSyncedEvent(String ownerType, Long ownerId, List<String> urls) {
}
