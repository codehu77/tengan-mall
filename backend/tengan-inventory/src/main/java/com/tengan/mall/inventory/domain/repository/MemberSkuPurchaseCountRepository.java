package com.tengan.mall.inventory.domain.repository;

/**
 * 比照 WareSkuRepository 的精神：不用 select-then-update，靠條件式 UPDATE + 受影響列數判斷原子性，
 * 避免併發下多個請求同時通過限購檢查。
 */
public interface MemberSkuPurchaseCountRepository {

    /** purchased_count+count 不超過 limit 才成功；該 (memberId, skuId) 還沒有列時視同 0 件已購買。 */
    boolean tryIncrement(Long memberId, Long skuId, int count, int limit);

    /** lock() 同一次呼叫中其他 item 失敗時的補償：把剛才遞增的量扣回去。 */
    void decrement(Long memberId, Long skuId, int count);
}
