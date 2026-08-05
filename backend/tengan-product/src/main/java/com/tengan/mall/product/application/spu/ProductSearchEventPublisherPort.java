package com.tengan.mall.product.application.spu;

import java.util.List;

/** 呼叫端命名（不是 RabbitPublisherPort）——真正的技術實作（RabbitMQ）在 infrastructure 層。 */
public interface ProductSearchEventPublisherPort {

    void publishUpserted(List<SkuSearchDocumentPayload> skus);

    void publishRemoved(Long spuId, List<Long> skuIds);
}
