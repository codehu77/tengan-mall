package com.tengan.mall.seckill.infrastructure.product;

import java.math.BigDecimal;
import java.util.List;

/** tengan-product /internal/products/skus 回應的子集——只取展示端點用得到的欄位，images 忽略；
 * saleAttrValues 用來組 SPU 分組卡片的規格標籤（見 ProductAdapter）。 */
public record SkuDetailDto(Long id, Long spuId, String name, BigDecimal price, String mainImage,
        List<SkuSaleAttrValueDto> saleAttrValues) {
}
