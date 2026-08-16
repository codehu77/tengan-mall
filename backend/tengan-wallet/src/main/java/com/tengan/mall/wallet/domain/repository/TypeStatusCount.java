package com.tengan.mall.wallet.domain.repository;

/** GROUP BY type, status 的原始查詢結果，跟 PointsTransactionRepository#countGroupedByTypeAndStatus 搭配。 */
public record TypeStatusCount(int type, int status, long count) {
}
