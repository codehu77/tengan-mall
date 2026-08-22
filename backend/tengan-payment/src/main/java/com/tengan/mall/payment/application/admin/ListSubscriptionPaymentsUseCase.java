package com.tengan.mall.payment.application.admin;

import java.util.List;

public interface ListSubscriptionPaymentsUseCase {

    List<SubscriptionPaymentView> list(Long subscriptionId);
}
