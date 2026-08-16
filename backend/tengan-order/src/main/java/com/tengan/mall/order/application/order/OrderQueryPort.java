package com.tengan.mall.order.application.order;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * CQRS-lite 唯讀查詢，不透過 OrderRepository（那個介面服務建立/狀態轉換流程），下推分頁到 SQL 層
 * （見 ddd-standards.md 第五節）。memberId 為 null 代表不限會員（後台視角查全部）。
 */
public interface OrderQueryPort {

    List<OrderSummary> search(Long memberId, Integer status, int pageNum, int pageSize);

    long countSearch(Long memberId, Integer status);

    Optional<OrderDetailView> findDetailByOrderSn(String orderSn);

    long countCreatedToday();

    /** status=COMPLETED AND points_credited=false AND receipt_time<=cutoff，供 PointsGrantScheduler 掃描。 */
    List<PointsGrantCandidate> findPendingPointsCredit(Instant cutoff, int limit);
}
