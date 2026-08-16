package com.tengan.mall.payment.application.admin;

import com.tengan.mall.payment.domain.model.PaymentMethodOperLog;
import com.tengan.mall.payment.domain.repository.PaymentMethodConfigRepository;
import com.tengan.mall.payment.domain.repository.PaymentMethodOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePaymentMethodStatusService implements UpdatePaymentMethodStatusUseCase {

    private final PaymentMethodConfigRepository paymentMethodConfigRepository;
    private final PaymentMethodOperLogRepository paymentMethodOperLogRepository;

    public UpdatePaymentMethodStatusService(PaymentMethodConfigRepository paymentMethodConfigRepository,
            PaymentMethodOperLogRepository paymentMethodOperLogRepository) {
        this.paymentMethodConfigRepository = paymentMethodConfigRepository;
        this.paymentMethodOperLogRepository = paymentMethodOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdatePaymentMethodStatusCommand command) {
        paymentMethodConfigRepository.updateEnabled(command.method(), command.enabled());
        String action = command.enabled() ? "啟用付款方式" : "停用付款方式";
        paymentMethodOperLogRepository.save(PaymentMethodOperLog.create(command.operator(), "payment_method",
                action, "method=" + command.method()));
    }
}
