package com.tengan.mall.product.domain.exception;

/** 更新商品時，command 帶的 skuId 不屬於這個 Spu 目前的既有 SKU 清單——SpuCompositionAssembler 用。
 * 防止把別的 SPU 底下的 skuId 填進來，讓 updateById 誤改到別的商品的 SKU 列。 */
public class SkuIdMismatchException extends RuntimeException {

    public SkuIdMismatchException(Long skuId, Long spuId) {
        super("skuId=" + skuId + " 不屬於 spuId=" + spuId + " 目前的 SKU 清單");
    }
}
