package com.tengan.mall.payment.application.admin;

import com.tengan.mall.payment.domain.model.PaymentMethodConfig;
import com.tengan.mall.payment.domain.repository.PaymentMethodConfigRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListPaymentMethodConfigsService implements ListPaymentMethodConfigsUseCase {

    private final PaymentMethodConfigRepository paymentMethodConfigRepository;

    public ListPaymentMethodConfigsService(PaymentMethodConfigRepository paymentMethodConfigRepository) {
        this.paymentMethodConfigRepository = paymentMethodConfigRepository;
    }

    @Override
    public List<PaymentMethodConfig> list() {
        return paymentMethodConfigRepository.findAll();
    }
}
