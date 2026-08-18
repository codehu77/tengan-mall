package com.tengan.mall.payment.application.subscription;

public interface GetMySubscriptionUseCase {

    MySubscriptionView get(Long memberId);
}
