package com.tengan.mall.seckill.domain.repository;

import com.tengan.mall.seckill.domain.model.SeckillSku;
import java.time.Instant;
import java.util.List;

public interface SeckillSkuRepository {

    /** 整批覆蓋一個活動底下的 SKU 清單（先刪後插），對應 `PUT .../skus` 的「重設」語意。 */
    List<SeckillSku> replaceForActivity(Long activityId, List<SeckillSku> skus);

    /** 只覆蓋 skuIdsScope 範圍內的既有列（先刪後插），其餘不屬於這個範圍的既有列不受影響——
     * 供「一個活動綁多個商品，逐一新增/編輯/刪除某一個商品」用，不用每次都整批覆蓋全部商品
     * （見「設定活動商品改成列表頁」規劃）。skuIdsScope 通常是呼叫端已知的「這個商品目前全部的規格」，
     * newSkus 是這個商品要存的最終結果（可能是子集，甚至是空清單代表整個商品從活動移除）。 */
    List<SeckillSku> replaceForActivityAndSkuIds(Long activityId, List<Long> skuIdsScope, List<SeckillSku> newSkus);

    List<SeckillSku> findByActivityId(Long activityId);

    /** 兩段式查詢用（先查活動 id 清單，再用這支查子表），不對 seckill_activity/seckill_sku 下 JOIN。 */
    List<SeckillSku> findUnsettledByActivityIds(List<Long> activityIds);

    /** 條件式 UPDATE（WHERE id=? AND settled_at IS NULL），回傳是否真的寫入，做結算冪等判斷。 */
    boolean settle(Long id, int soldCount, Instant settledAt);
}
