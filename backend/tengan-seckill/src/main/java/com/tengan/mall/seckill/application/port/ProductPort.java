package com.tengan.mall.seckill.application.port;

import java.util.List;

/** 呼叫 tengan-product 的 /internal/products/skus 批次端點，補上公開展示端點需要的商品資訊。 */
public interface ProductPort {

    List<SkuInfo> batchGet(List<Long> skuIds);
}
