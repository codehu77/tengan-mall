package com.tengan.mall.seckill.domain.repository;

import com.tengan.mall.seckill.domain.model.SeckillSku;
import java.time.Instant;
import java.util.List;

public interface SeckillSkuRepository {

    /** 整批覆蓋一個活動底下的 SKU 清單（先刪後插），對應 `PUT .../skus` 的「重設」語意。 */
    List<SeckillSku> replaceForActivity(Long activityId, List<SeckillSku> skus);

    List<SeckillSku> findByActivityId(Long activityId);

    /** 兩段式查詢用（先查活動 id 清單，再用這支查子表），不對 seckill_activity/seckill_sku 下 JOIN。 */
    List<SeckillSku> findUnsettledByActivityIds(List<Long> activityIds);

    /** 條件式 UPDATE（WHERE id=? AND settled_at IS NULL），回傳是否真的寫入，做結算冪等判斷。 */
    boolean settle(Long id, int soldCount, Instant settledAt);
}
