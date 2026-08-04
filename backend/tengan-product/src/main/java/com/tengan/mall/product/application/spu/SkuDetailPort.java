package com.tengan.mall.product.application.spu;

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
}
