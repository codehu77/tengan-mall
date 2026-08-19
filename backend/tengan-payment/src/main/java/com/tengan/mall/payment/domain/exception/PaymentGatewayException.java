package com.tengan.mall.payment.domain.exception;

/** 呼叫金流商 API 本身失敗或回傳失敗結果（非我方資料問題）。 */
public class PaymentGatewayException extends RuntimeException {

    public PaymentGatewayException(String message) {
        super(message);
    }

    public PaymentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
