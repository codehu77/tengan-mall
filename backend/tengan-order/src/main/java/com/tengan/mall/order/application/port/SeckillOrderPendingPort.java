package com.tengan.mall.order.application.port;

import java.util.Optional;

/**
 * 秒殺訂單改走非同步落地後，提交當下到真正寫進 DB 之間有一段空窗期——這個 port 讓查詢端點能區分
 * 「還在處理中」跟「真的查無此單」兩種狀態（見 Phase 9 規劃第 5 節）。
 */
public interface SeckillOrderPendingPort {

    void markPending(String orderSn, Long memberId);

    /** 回傳 orderSn 對應的 memberId（供查詢端點核對是不是自己的訂單）；查無標記代表不是處理中，可能是真的查無此單或標記已過期。 */
    Optional<Long> findPendingMemberId(String orderSn);
}
