package com.tengan.mall.payment.application.subscription;

import java.time.Instant;

/** subscribed=false 代表從沒訂閱過或已經完全過期；其餘欄位在 subscribed=false 時為 null。 */
public record MySubscriptionView(boolean subscribed, String targetTier, String status, Instant paidUntil,
        boolean autoRenew) {

    public static MySubscriptionView none() {
        return new MySubscriptionView(false, null, null, null, false);
    }
}
