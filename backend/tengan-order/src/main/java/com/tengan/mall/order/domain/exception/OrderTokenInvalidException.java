package com.tengan.mall.order.domain.exception;

/** orderToken 比對失敗（不存在/已被使用/過期）——擋連點結帳按鈕或網路重試造成的重複下單。 */
public class OrderTokenInvalidException extends RuntimeException {

    public OrderTokenInvalidException(Long memberId) {
        super("訂單重複提交或 token 已過期: memberId=" + memberId);
    }
}
