package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.Spu;
import org.springframework.stereotype.Component;

/** GetSpuDetailService（內部，任何狀態）跟 GetPublicSpuDetailService（前台，只回 ON_SHELF）共用的映射邏輯。 */
@Component
class SpuDetailAssembler {

    GetSpuDetailResult toResult(Spu spu) {
        var attrValues = spu.getAttrValues().stream()
                .map(v -> new SpuBaseAttrValueView(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        var images = spu.getImages().stream()
                .map(i -> new SpuImageView(i.imageUrl(), i.sort()))
                .toList();
        var skus = spu.getSkus().stream().map(sku -> toSkuView(spu.getId(), sku)).toList();
        return new GetSpuDetailResult(spu.getId(), spu.getCategoryId(), spu.getBrandId(), spu.getName(),
                spu.getDescription(), spu.getMainImage(), spu.getStatus().getValue(), attrValues, images, skus);
    }

    private SkuDetailView toSkuView(Long spuId, Sku sku) {
        var images = sku.getImages().stream()
                .map(i -> new SkuImageView(i.imageUrl(), i.sort()))
                .toList();
        var saleAttrValues = sku.getSaleAttrValues().stream()
                .map(v -> new SkuSaleAttrValueView(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        return new SkuDetailView(sku.getId(), spuId, sku.getName(), sku.getPrice(), sku.getMainImage(),
                sku.getSaleCount(), sku.getSort(), images, saleAttrValues);
    }
}
