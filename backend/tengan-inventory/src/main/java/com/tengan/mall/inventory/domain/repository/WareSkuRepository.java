package com.tengan.mall.inventory.domain.repository;

import java.util.List;

/**
 * 不走傳統 load-mutate-save——lock/release/deduct 本質是高併發下的原子條件更新，讀出聚合再存回去
 * 中間有時間窗口會超賣，這是 docs/資料庫設計規範.md 點名要用條件式 UPDATE 的典型案例，所以直接暴露
 * 條件式 SQL 意圖方法，由 infrastructure 層用 MyBatis-Plus 自訂 @Update 實作。
 */
public interface WareSkuRepository {

    /** 彙總所有倉庫的可用庫存（stock-locked_stock），customer check 端點用，不鎖定。 */
    int sumAvailableStock(Long skuId);

    /** 依 ware_id 排序的候選倉庫清單，lock/seckill-deduct 依序嘗試用（first-fit 簡化策略）。 */
    List<Long> findCandidateWareIds(Long skuId);

    /** 條件式 UPDATE locked_stock+=count WHERE stock-locked_stock>=count，受影響列數>0 才算成功。 */
    boolean tryLock(Long wareId, Long skuId, int count);

    /** 條件式 UPDATE locked_stock-=count WHERE locked_stock>=count。 */
    void release(Long wareId, Long skuId, int count);

    /** 條件式 UPDATE stock-=count, locked_stock-=count WHERE locked_stock>=count。 */
    void deduct(Long wareId, Long skuId, int count);

    /** 秒殺結算專用：跳過 locked_stock 記帳，直接條件式 UPDATE stock-=count WHERE stock>=count。 */
    boolean deductStockOnly(Long wareId, Long skuId, int count);

    /** 該 (wareId, skuId) 是否已經有庫存列——用來分辨「新增庫存」跟「調整庫存」兩個不同動作。 */
    boolean existsForWareSku(Long wareId, Long skuId);

    /**
     * 後台手動新增全新庫存列（沒有舊值可覆蓋，用絕對值合理）。已存在該 (wareId, skuId) 就不動作、
     * 回傳 false（交給呼叫端判斷要不要改走 adjustStockDelta）。
     */
    boolean tryCreateInitialStock(Long wareId, Long skuId, int initialStock);

    /**
     * 後台人工盤點修正：增減量（可正可負）而不是覆蓋絕對值——比照 lock/release/deduct 同樣的條件式
     * UPDATE 精神，`stock+delta>=0` 防止修正後變成負庫存，也避免兩個管理員前後腳覆蓋掉彼此的異動。
     * 該列不存在（還沒 tryCreateInitialStock 過）或會變成負庫存都回傳 false。
     */
    boolean adjustStockDelta(Long wareId, Long skuId, int delta);
}
