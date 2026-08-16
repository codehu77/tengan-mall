package com.tengan.mall.payment.domain.repository;

import com.tengan.mall.payment.domain.model.PaymentMethodOperLog;

public interface PaymentMethodOperLogRepository {

    PaymentMethodOperLog save(PaymentMethodOperLog operLog);
}
