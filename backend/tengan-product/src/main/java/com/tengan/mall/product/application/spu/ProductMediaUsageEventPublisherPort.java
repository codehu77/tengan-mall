package com.tengan.mall.product.application.spu;

import java.util.List;

/**
 * 呼叫端命名（不是 RabbitPublisherPort）——真正的技術實作（RabbitMQ）在 infrastructure 層。
 * tengan-media 消費這個事件維護 media_asset 生命週期追蹤（CONFIRMED/PENDING），取代舊版直接呼叫
 * tengan-media REST 刪除端點的做法。宣告的是 owner 目前「完整」引用的 urls（不是增量 attach/
 * detach），tengan-media 自己算差集決定哪些該被打回 PENDING。跟 ProductLaunchConfigEventPublisherPort
 * 同一種性質：不管 SPU 是不是 ON_SHELF，只要存檔就發，這是內部側效清理同步，不是公開可見性判斷。
 */
public interface ProductMediaUsageEventPublisherPort {

    /** 刪除整顆 SPU 時 urls 傳空 list，等同宣告「這個 owner 現在什麼都不用」。 */
    void publishSynced(Long spuId, List<String> urls);
}
