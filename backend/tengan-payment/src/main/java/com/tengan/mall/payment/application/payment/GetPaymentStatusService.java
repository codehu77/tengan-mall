package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.domain.exception.PaymentRecordNotFoundException;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class GetPaymentStatusService implements GetPaymentStatusUseCase {

    private final PaymentRecordRepository paymentRecordRepository;

    public GetPaymentStatusService(PaymentRecordRepository paymentRecordRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
    }

    @Override
    public PaymentStatusView getStatus(String orderSn) {
        var record = paymentRecordRepository.findByOrderSn(orderSn)
                .orElseThrow(() -> new PaymentRecordNotFoundException(orderSn));
        return new PaymentStatusView(record.getOrderSn(), record.getMethod(), record.getStatus().getValue(),
                record.getAmount());
    }
}
