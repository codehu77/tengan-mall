package com.tengan.mall.payment.domain.exception;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(Long memberId) {
        super("找不到進行中的訂閱: memberId=" + memberId);
    }

    private SubscriptionNotFoundException(String message) {
        super(message);
    }

    public static SubscriptionNotFoundException byMerchantTradeNo(String merchantTradeNo) {
        return new SubscriptionNotFoundException("找不到對應的訂閱合約: merchantTradeNo=" + merchantTradeNo);
    }
}
