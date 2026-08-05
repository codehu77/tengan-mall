package com.tengan.mall.search.interfaces.mq;

import com.tengan.mall.search.application.IndexSkuDocumentsUseCase;
import com.tengan.mall.search.application.RemoveSkuDocumentsUseCase;
import com.tengan.mall.search.application.SkuSearchAttrValue;
import com.tengan.mall.search.application.SkuSearchDocument;
import com.tengan.mall.search.infrastructure.mq.ProductRemovedEvent;
import com.tengan.mall.search.infrastructure.mq.ProductUpsertedEvent;
import com.tengan.mall.search.infrastructure.mq.RabbitConfig;
import com.tengan.mall.search.infrastructure.mq.SkuUpsertPayload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

    private final IndexSkuDocumentsUseCase indexSkuDocumentsUseCase;
    private final RemoveSkuDocumentsUseCase removeSkuDocumentsUseCase;

    public ProductEventListener(IndexSkuDocumentsUseCase indexSkuDocumentsUseCase,
            RemoveSkuDocumentsUseCase removeSkuDocumentsUseCase) {
        this.indexSkuDocumentsUseCase = indexSkuDocumentsUseCase;
        this.removeSkuDocumentsUseCase = removeSkuDocumentsUseCase;
    }

    @RabbitListener(queues = RabbitConfig.UPSERTED_QUEUE)
    public void onUpserted(ProductUpsertedEvent event) {
        indexSkuDocumentsUseCase.index(event.skus().stream().map(this::toDocument).toList());
    }

    @RabbitListener(queues = RabbitConfig.REMOVED_QUEUE)
    public void onRemoved(ProductRemovedEvent event) {
        removeSkuDocumentsUseCase.remove(event.skuIds());
    }

    private SkuSearchDocument toDocument(SkuUpsertPayload p) {
        var attrs = p.attrs() == null ? null
                : p.attrs().stream()
                        .map(a -> new SkuSearchAttrValue(a.attrType() + "-" + a.attrId(), a.attrId(), a.attrName(),
                                a.attrValue()))
                        .toList();
        Double price = p.price() == null ? null : p.price().doubleValue();
        return new SkuSearchDocument(p.skuId(), p.spuId(), p.skuName(), p.spuName(), price, p.mainImage(),
                p.saleCount(), p.brandId(), p.brandName(), p.catalog1Id(), p.catalog1Name(), p.catalog2Id(),
                p.catalog2Name(), p.catalog3Id(), p.catalog3Name(), attrs);
    }
}
