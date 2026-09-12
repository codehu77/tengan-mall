package com.tengan.mall.search.interfaces.mq;

import com.tengan.mall.search.application.IndexSpuDocumentUseCase;
import com.tengan.mall.search.application.RemoveSpuDocumentUseCase;
import com.tengan.mall.search.application.SkuSearchAttrValue;
import com.tengan.mall.search.application.SkuVariant;
import com.tengan.mall.search.application.SpuSearchDocument;
import com.tengan.mall.search.infrastructure.mq.ProductRemovedEvent;
import com.tengan.mall.search.infrastructure.mq.ProductUpsertedEvent;
import com.tengan.mall.search.infrastructure.mq.RabbitConfig;
import com.tengan.mall.search.infrastructure.mq.SkuAttrPayload;
import com.tengan.mall.search.infrastructure.mq.SkuVariantUpsertPayload;
import com.tengan.mall.search.infrastructure.mq.SpuUpsertPayload;
import java.util.List;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

    private final IndexSpuDocumentUseCase indexSpuDocumentUseCase;
    private final RemoveSpuDocumentUseCase removeSpuDocumentUseCase;

    public ProductEventListener(IndexSpuDocumentUseCase indexSpuDocumentUseCase,
            RemoveSpuDocumentUseCase removeSpuDocumentUseCase) {
        this.indexSpuDocumentUseCase = indexSpuDocumentUseCase;
        this.removeSpuDocumentUseCase = removeSpuDocumentUseCase;
    }

    @RabbitListener(queues = RabbitConfig.UPSERTED_QUEUE)
    public void onUpserted(ProductUpsertedEvent event) {
        indexSpuDocumentUseCase.index(toDocument(event.spu()));
    }

    @RabbitListener(queues = RabbitConfig.REMOVED_QUEUE)
    public void onRemoved(ProductRemovedEvent event) {
        removeSpuDocumentUseCase.remove(event.spuId());
    }

    private SpuSearchDocument toDocument(SpuUpsertPayload p) {
        var baseAttrs = toAttrValues(p.baseAttrs());
        List<SkuVariant> skus = p.skus() == null ? null : p.skus().stream().map(this::toVariant).toList();
        Double minPrice = p.minPrice() == null ? null : p.minPrice().doubleValue();
        Double maxPrice = p.maxPrice() == null ? null : p.maxPrice().doubleValue();
        return new SpuSearchDocument(p.spuId(), p.spuName(), p.spuMainImage(), minPrice, maxPrice, p.saleCount(),
                p.brandId(), p.brandName(), p.catalog1Id(), p.catalog1Name(), p.catalog2Id(), p.catalog2Name(),
                p.catalog3Id(), p.catalog3Name(), baseAttrs, skus);
    }

    private SkuVariant toVariant(SkuVariantUpsertPayload v) {
        Double price = v.price() == null ? null : v.price().doubleValue();
        return new SkuVariant(v.skuId(), v.skuName(), price, v.mainImage(), v.saleCount(), toAttrValues(v.saleAttrs()));
    }

    private List<SkuSearchAttrValue> toAttrValues(List<SkuAttrPayload> attrs) {
        return attrs == null ? null
                : attrs.stream()
                        .map(a -> new SkuSearchAttrValue(a.attrType() + "-" + a.attrId(), a.attrId(), a.attrName(),
                                a.attrValue()))
                        .toList();
    }
}
