package com.tengan.mall.product.application.spu;

import java.math.BigDecimal;
import java.util.List;

/** id 為 null 代表新規格；非 null 代表編輯既有規格，SpuCompositionAssembler 會驗證這個 id 真的屬於這個 SPU。 */
public record SkuCommand(Long id, String name, BigDecimal price, String mainImage, int sort,
        List<SkuImageCommand> images, List<SkuSaleAttrValueCommand> saleAttrValues, Integer purchaseLimitPerUser) {
}
