package com.tengan.mall.payment.application.payment;

import java.util.List;

public interface GetEnabledPaymentMethodsUseCase {

    List<String> getEnabledMethods();
}
