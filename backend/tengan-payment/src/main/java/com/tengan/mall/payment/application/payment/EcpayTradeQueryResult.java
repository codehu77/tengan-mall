package com.tengan.mall.payment.application.payment;

/**
 * ECPay {@code QueryTradeInfo/V5} 查詢結果，供 credit_card 重試/排程式查帳共用的同步查詢流程使用。
 * {@code tradeStatus}：0=未付款、1=已付款、10200095=消費者未完成付款作業（見官方文件）。
 */
public record EcpayTradeQueryResult(String tradeStatus, String tradeNo, String paymentDate) {

    public boolean isPaid() {
        return "1".equals(tradeStatus);
    }
}
