package com.tengan.mall.product.domain.repository;

/**
 * Sku 是 Spu 聚合根底下沒有獨立 Repository 的 child entity（正常編輯走 Spu.replaceSkus() 整批替換），
 * 但 sale_count 遞增是訂單完成觸發的旁支寫入，跟整批替換的編輯路徑無關——比照 tengan-inventory
 * DeductInventoryService 直接打 WareSkuRepository、繞過任何「更大聚合」的模式，直接對 sku 表下
 * 條件式 UPDATE，不透過 Spu 聚合根。
 */
public interface SkuSaleCountRepository {

    void increment(Long skuId, int delta);
}
