package com.tengan.mall.seckill.application.port;

import java.math.BigDecimal;

/** variantLabel 是這顆 SKU 的規格標籤（例如「黑色/256G」），供 SPU 分組展示用，來源是 tengan-product 的 saleAttrValues。 */
public record SkuInfo(Long skuId, Long spuId, String name, String mainImage, BigDecimal price, String variantLabel) {
}
