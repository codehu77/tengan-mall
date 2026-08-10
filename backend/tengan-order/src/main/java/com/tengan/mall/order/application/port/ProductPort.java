package com.tengan.mall.order.application.port;

import java.util.List;

public interface ProductPort {

    /** 呼叫 tengan-product 的 GET /internal/products/skus?ids=——不信任前端送來的金額，一律即時查價。 */
    List<PricedSkuInfo> batchPrice(List<Long> skuIds);
}
