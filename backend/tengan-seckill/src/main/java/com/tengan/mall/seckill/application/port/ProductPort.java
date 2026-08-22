package com.tengan.mall.seckill.application.port;

import java.util.List;

/** 呼叫 tengan-product 的 /internal/products/skus 批次端點，補上公開展示端點需要的商品資訊。 */
public interface ProductPort {

    List<SkuInfo> batchGet(List<Long> skuIds);

    /** 供公開展示端點的 SPU 分組卡片補名稱/圖片用。 */
    List<SpuInfo> batchGetSpu(List<Long> spuIds);
}
