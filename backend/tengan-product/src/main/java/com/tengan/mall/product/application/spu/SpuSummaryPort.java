package com.tengan.mall.product.application.spu;

import java.util.List;

/**
 * CQRS-lite：跟 {@link SkuDetailPort} 同樣的理由——tengan-seckill 展示端點只需要 SPU 的
 * name/mainImage 兩個欄位，不需要載入完整聚合根（含全部 sku/attrValue/image），獨立開一個 Port
 * 直接查 spu 表、回傳攤平 DTO，不經過 SpuRepository。
 */
public interface SpuSummaryPort {

    List<SpuSummaryView> findByIds(List<Long> spuIds);
}
