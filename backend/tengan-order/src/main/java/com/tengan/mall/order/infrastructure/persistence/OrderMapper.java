package com.tengan.mall.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
}
