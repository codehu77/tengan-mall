package com.tengan.mall.order.application.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * CQRS-lite 唯讀查詢，不透過 OrderRepository（那個介面服務建立/狀態轉換流程），下推分頁到 SQL 層
 * （見 ddd-standards.md 第五節）。memberId 為 null 代表不限會員（後台視角查全部）。
 */
public interface OrderQueryPort {

    List<OrderSummary> search(Long memberId, Integer status, Instant createdFrom, Instant createdTo, int pageNum,
            int pageSize);

    long countSearch(Long memberId, Integer status, Instant createdFrom, Instant createdTo);

    Optional<OrderDetailView> findDetailByOrderSn(String orderSn);

    long countCreatedToday();

    /** 供 dashboard「今日/本月/本年營收」用，見 {@link com.tengan.mall.order.application.admin.GetOrderStatsTodayService}。 */
    BigDecimal revenueToday();

    BigDecimal revenueThisMonth();

    BigDecimal revenueThisYear();

    /** 近 days 天的每日營收，缺訂單的日期補 0，依日期升序，固定回傳 days 筆。 */
    List<DailyRevenue> revenueTrend(int days);

    /** status=COMPLETED AND points_credited=false AND receipt_time<=cutoff，供 PointsGrantScheduler 掃描。 */
    List<PointsGrantCandidate> findPendingPointsCredit(Instant cutoff, int limit);
}
