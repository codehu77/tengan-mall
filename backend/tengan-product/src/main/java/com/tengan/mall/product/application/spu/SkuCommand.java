package com.tengan.mall.product.application.spu;

import java.math.BigDecimal;
import java.util.List;

public record SkuCommand(String name, BigDecimal price, String mainImage, int sort, List<SkuImageCommand> images,
        List<SkuSaleAttrValueCommand> saleAttrValues) {
}
