package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecordPO> {

    @Update("UPDATE payment_record SET status = 2, gateway_trade_no = #{gatewayTradeNo}, paid_at = NOW() "
            + "WHERE order_sn = #{orderSn} AND status = 1")
    int markPaid(@Param("orderSn") String orderSn, @Param("gatewayTradeNo") String gatewayTradeNo);

    @Delete("DELETE FROM payment_record WHERE order_sn = #{orderSn} AND status = 1")
    int deleteIfPending(@Param("orderSn") String orderSn);
}
