package com.tengan.mall.payment.application.admin;

import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class ListPaymentRecordsService implements ListPaymentRecordsUseCase {

    private final PaymentRecordRepository paymentRecordRepository;

    public ListPaymentRecordsService(PaymentRecordRepository paymentRecordRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
    }

    @Override
    public ListPaymentRecordsResult list(ListPaymentRecordsQuery query) {
        var items = paymentRecordRepository.search(query.orderSn(), query.method(), query.page(), query.pageSize())
                .stream()
                .map(r -> new PaymentRecordView(r.getId(), r.getOrderSn(), r.getMemberId(), r.getMethod(),
                        r.getAmount(), r.getStatus().getValue(), r.getGatewayTradeNo(), r.getPaidAt(),
                        r.getCreatedAt()))
                .toList();
        long total = paymentRecordRepository.countSearch(query.orderSn(), query.method());
        return new ListPaymentRecordsResult(items, total);
    }
}
