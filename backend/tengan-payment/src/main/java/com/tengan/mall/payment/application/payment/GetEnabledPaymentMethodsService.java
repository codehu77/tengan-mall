package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.domain.repository.PaymentMethodConfigRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetEnabledPaymentMethodsService implements GetEnabledPaymentMethodsUseCase {

    private final PaymentMethodConfigRepository paymentMethodConfigRepository;

    public GetEnabledPaymentMethodsService(PaymentMethodConfigRepository paymentMethodConfigRepository) {
        this.paymentMethodConfigRepository = paymentMethodConfigRepository;
    }

    @Override
    public List<String> getEnabledMethods() {
        return paymentMethodConfigRepository.findEnabled().stream().map(m -> m.method()).toList();
    }
}
