package com.tengan.mall.wallet.application.points;

import java.time.Instant;

/** status 為 null 代表不限狀態；type/status 的轉譯規則見 interfaces.rest 層（PENDING 是 status 不是 type）。 */
public record ListPointsTransactionsQuery(Long memberId, Integer type, Integer status, Instant fromDate,
        String keyword, int pageNum, int pageSize) {
}
