package com.tengan.mall.payment.application.admin;

import com.tengan.mall.payment.domain.repository.SubscriptionPaymentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListSubscriptionPaymentsService implements ListSubscriptionPaymentsUseCase {

    private final SubscriptionPaymentRepository subscriptionPaymentRepository;

    public ListSubscriptionPaymentsService(SubscriptionPaymentRepository subscriptionPaymentRepository) {
        this.subscriptionPaymentRepository = subscriptionPaymentRepository;
    }

    @Override
    public List<SubscriptionPaymentView> list(Long subscriptionId) {
        return subscriptionPaymentRepository.findBySubscriptionId(subscriptionId).stream()
                .map(p -> new SubscriptionPaymentView(p.getId(), p.getGwsr(), p.isSuccess(), p.getAmount(),
                        p.getTotalSuccessTimes(), p.getProcessDate(), p.getCreatedAt()))
                .toList();
    }
}
