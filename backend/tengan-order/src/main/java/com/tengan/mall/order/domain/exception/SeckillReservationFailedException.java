package com.tengan.mall.order.domain.exception;

/**
 * 秒殺配額保留失敗（已售完/超過限購/活動剛好結束）——{@code reason} 是 tengan-seckill 回應裡的
 * {@code message} 原樣轉發，前端既有的通用 catch-all（讀 {@code e.data?.data?.message} 顯示 toast）
 * 會自動接住，不需要額外前端改動（見 Phase 9 前台整合規劃第 3 節）。
 */
public class SeckillReservationFailedException extends RuntimeException {

    public SeckillReservationFailedException(Long skuId, String reason) {
        super("skuId=" + skuId + " 秒殺保留失敗: " + reason);
    }
}
