package com.tengan.mall.product.infrastructure.mq;

import com.tengan.mall.product.application.spu.SpuSearchDocumentPayload;

public record ProductUpsertedEvent(SpuSearchDocumentPayload spu) {
}
