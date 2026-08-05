package com.tengan.mall.product.infrastructure.mq;

import com.tengan.mall.product.application.spu.SkuSearchDocumentPayload;
import java.util.List;

public record ProductUpsertedEvent(List<SkuSearchDocumentPayload> skus) {
}
