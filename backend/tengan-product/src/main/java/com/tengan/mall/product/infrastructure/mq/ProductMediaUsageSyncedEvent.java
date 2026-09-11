package com.tengan.mall.product.infrastructure.mq;

import java.util.List;

public record ProductMediaUsageSyncedEvent(String ownerType, Long ownerId, List<String> urls) {
}
