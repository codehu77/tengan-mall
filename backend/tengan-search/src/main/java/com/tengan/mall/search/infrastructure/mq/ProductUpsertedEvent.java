package com.tengan.mall.search.infrastructure.mq;

import java.util.List;

public record ProductUpsertedEvent(List<SkuUpsertPayload> skus) {
}
