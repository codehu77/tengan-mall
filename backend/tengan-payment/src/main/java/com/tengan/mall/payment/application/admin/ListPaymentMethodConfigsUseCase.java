package com.tengan.mall.payment.application.admin;

import com.tengan.mall.payment.domain.model.PaymentMethodConfig;
import java.util.List;

public interface ListPaymentMethodConfigsUseCase {

    List<PaymentMethodConfig> list();
}
