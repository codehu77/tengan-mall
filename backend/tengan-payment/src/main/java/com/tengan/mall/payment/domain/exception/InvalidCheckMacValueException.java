package com.tengan.mall.payment.domain.exception;

/** ECPay callback 的 CheckMacValue 驗算不吻合，可能是資料被竄改或憑證設定錯誤。 */
public class InvalidCheckMacValueException extends RuntimeException {

    public InvalidCheckMacValueException() {
        super("CheckMacValue 驗證失敗");
    }
}
