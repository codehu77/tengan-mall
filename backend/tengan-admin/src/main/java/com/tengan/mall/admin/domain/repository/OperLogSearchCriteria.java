package com.tengan.mall.admin.domain.repository;

import java.time.Instant;

/** 查詢用參數物件，純粹收斂 {@link OperLogRepository#search} 的參數數量，沒有任何不變條件。 */
public record OperLogSearchCriteria(Long adminUserId, String module, Instant from, Instant to) {
}
