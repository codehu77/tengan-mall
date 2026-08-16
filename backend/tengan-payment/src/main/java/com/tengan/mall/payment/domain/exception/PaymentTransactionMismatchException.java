package com.tengan.mall.payment.domain.exception;

/** LINE Pay confirm 重複呼叫但 transactionId 對不上（防偽），或關聯的 gatewayTradeNo 不吻合。 */
public class PaymentTransactionMismatchException extends RuntimeException {

    public PaymentTransactionMismatchException(String orderSn) {
        super("付款交易資訊不吻合，拒絕處理: orderSn=" + orderSn);
    }
}
