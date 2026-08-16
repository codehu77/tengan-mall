package com.tengan.mall.payment.domain.repository;

import com.tengan.mall.payment.domain.model.PaymentMethodConfig;
import java.util.List;

public interface PaymentMethodConfigRepository {

    List<PaymentMethodConfig> findAll();

    List<PaymentMethodConfig> findEnabled();

    boolean updateEnabled(String method, boolean enabled);
}
