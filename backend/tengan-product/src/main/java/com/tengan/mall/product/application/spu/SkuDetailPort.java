package com.tengan.mall.product.application.spu;

import java.util.List;
import java.util.Optional;

/**
 * CQRS-lite：前台高頻讀取（購物車/訂單之後要查「這顆 sku 現在多少錢」）不該為了一筆 sku 去載入整個
 * Spu 聚合根(含所有兄弟 sku)，所以獨立開一個 Port 直接查 sku 相關表、回傳攤平 DTO，不經過
 * SpuRepository（見 Spu 聚合根合併 Sku 時的設計討論：這是付出的代價只在低頻寫入路徑、不影響高頻讀取
 * 路徑的前提）。實作放在 infrastructure/persistence，用跟 SpuRepositoryImpl 相同的 MyBatis-Plus
 * 技術，但是完全獨立的 adapter class。
 */
public interface SkuDetailPort {

    Optional<SkuDetailView> findById(Long skuId);

    /**
     * 給 tengan-cart 這種要一次查多顆 sku 即時價格的情境用，避免逐筆呼叫造成 N+1。跟 findById 一樣
     * 不套上架過濾——sku 被下架後仍要能查到目前價格/名稱（購物車要顯示，不是直接讓項目消失）；
     * 已完全刪除的 skuId 會被靜默略過，回傳的清單不保證跟輸入的 ids 等長。
     */
    List<SkuDetailView> findByIds(List<Long> skuIds);
}
