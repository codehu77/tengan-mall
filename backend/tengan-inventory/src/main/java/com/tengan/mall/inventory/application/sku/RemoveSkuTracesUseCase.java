package com.tengan.mall.inventory.application.sku;

import java.util.List;

public interface RemoveSkuTracesUseCase {

    /**
     * 商品規格真的被刪除時（SPU 整顆刪除，或編輯商品整批替換 SKU），把 tengan-inventory 對這批 skuId
     * 擁有的所有東西一次清乾淨——不是只清某一張表。新增任何 skuId 相關的表/Redis key 時，都要記得
     * 補進這支方法，不要讓 MQ listener 直接散落呼叫各個 repository（見 .docs/資料庫設計規範.md
     * 「跨服務刪除連動」章節）。
     */
    void remove(List<Long> skuIds);
}
