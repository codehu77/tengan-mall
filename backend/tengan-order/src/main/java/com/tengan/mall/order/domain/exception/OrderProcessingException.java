package com.tengan.mall.order.domain.exception;

/**
 * 秒殺訂單非同步落地中，還沒真正寫進 DB——跟「查無此單」（{@link OrderNotFoundException}）要分開處理，
 * 讓前端知道要繼續輪詢而不是顯示錯誤（見 Phase 9 規劃第 5 節）。
 */
public class OrderProcessingException extends RuntimeException {

    public OrderProcessingException(String orderSn) {
        super("訂單處理中: orderSn=" + orderSn);
    }
}
