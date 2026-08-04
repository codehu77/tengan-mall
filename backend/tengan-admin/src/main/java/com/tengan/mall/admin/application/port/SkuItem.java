package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.util.List;

public record SkuItem(Long id, Long spuId, String name, BigDecimal price, String mainImage, int saleCount, int sort,
        List<SkuImageItem> images, List<SkuSaleAttrValueItem> saleAttrValues) {
}
