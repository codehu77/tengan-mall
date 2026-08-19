package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecordPO> {

    @Update("UPDATE payment_record SET status = 2, gateway_trade_no = #{gatewayTradeNo}, paid_at = NOW() "
            + "WHERE id = #{id} AND status = 1")
    int markPaid(@Param("id") Long id, @Param("gatewayTradeNo") String gatewayTradeNo);

    /** 同步查帳確認這筆嘗試沒有真的付款成功，結案存查（不刪除，保留給排程式查帳當歷史依據）。 */
    @Update("UPDATE payment_record SET status = 3 WHERE id = #{id} AND status = 1")
    int markFailed(@Param("id") Long id);

    /** 只給 cod/linepay 用——這兩種付款方式沒有 ECPay MerchantTradeNo 唯一性問題，重試直接刪舊記錄重來。 */
    @Delete("DELETE FROM payment_record WHERE order_sn = #{orderSn} AND status = 1")
    int deleteIfPending(@Param("orderSn") String orderSn);
}
