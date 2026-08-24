package com.tengan.mall.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 狀態轉換一律條件式 UPDATE（比照 PurchaseOrderMapper/WareOrderTaskMapper），SQL 裡的數字直接對應
 * OrderStatus（@Update 手寫 SQL 不走 IEnum 轉換）：1=PENDING_PAYMENT 2=PAID 3=SHIPPED 4=COMPLETED 5=CANCELLED。
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> {

    @Update("UPDATE `order` SET status = 5, cancel_reason = #{reason} WHERE order_sn = #{orderSn} AND status = 1")
    int markCancelled(@Param("orderSn") String orderSn, @Param("reason") String reason);

    @Update("UPDATE `order` SET status = 2 WHERE order_sn = #{orderSn} AND status = 1")
    int markPaid(@Param("orderSn") String orderSn);

    @Update("UPDATE `order` SET status = 3 WHERE order_sn = #{orderSn} AND status = 2")
    int markShipped(@Param("orderSn") String orderSn);

    @Update("UPDATE `order` SET status = 4, receipt_time = NOW() WHERE order_sn = #{orderSn} AND status = 3")
    int markCompleted(@Param("orderSn") String orderSn);

    @Update("UPDATE `order` SET points_credited = TRUE WHERE order_sn = #{orderSn} AND points_credited = FALSE")
    int markPointsCredited(@Param("orderSn") String orderSn);

    @Select("SELECT * FROM `order` WHERE status = 4 AND points_credited = FALSE AND receipt_time <= #{cutoff} "
            + "ORDER BY receipt_time ASC LIMIT #{limit}")
    List<OrderPO> findPendingPointsCredit(@Param("cutoff") LocalDateTime cutoff, @Param("limit") int limit);

    /** 只算已成交（PAID/SHIPPED/COMPLETED）訂單，未付款/已取消不計入營收；today/month/year 三個 dashboard
     * 指標共用同一支查詢，差別只在呼叫端傳的 since 邊界不同（今天/本月/本年第一天 00:00）。 */
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM `order` "
            + "WHERE created_at >= #{since} AND status IN (2, 3, 4)")
    BigDecimal sumRevenueSince(@Param("since") LocalDateTime since);

    /** 稀疏列（沒有訂單的日期不會出現在結果裡），由呼叫端補齊缺的日期。 */
    @Select("SELECT DATE(created_at) AS day, COALESCE(SUM(pay_amount), 0) AS revenue FROM `order` "
            + "WHERE created_at >= #{since} AND status IN (2, 3, 4) GROUP BY DATE(created_at)")
    List<DailyRevenueRow> findDailyRevenueSince(@Param("since") LocalDateTime since);
}
