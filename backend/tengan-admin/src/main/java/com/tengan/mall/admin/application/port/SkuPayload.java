package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.util.List;

public record SkuPayload(String name, BigDecimal price, String mainImage, int sort, List<SkuImagePayload> images,
        List<SkuSaleAttrValuePayload> saleAttrValues) {
}
