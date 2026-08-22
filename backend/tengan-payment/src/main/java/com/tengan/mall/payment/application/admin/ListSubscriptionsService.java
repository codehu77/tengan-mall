package com.tengan.mall.payment.application.admin;

import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class ListSubscriptionsService implements ListSubscriptionsUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public ListSubscriptionsService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public ListSubscriptionsResult list(ListSubscriptionsQuery query) {
        var items = subscriptionRepository.search(query.memberId(), query.status(), query.page(), query.pageSize())
                .stream()
                .map(s -> new SubscriptionView(s.getId(), s.getMemberId(), s.getTargetTier(), s.getStatus().getValue(),
                        s.getEcpayMerchantTradeNo(), s.getPeriodAmount(), s.getConsecutiveFailures(),
                        s.getPaidUntil(), s.getBenefitExpiredAt(), s.getCreatedAt(), s.getCancelledAt()))
                .toList();
        long total = subscriptionRepository.countSearch(query.memberId(), query.status());
        return new ListSubscriptionsResult(items, total);
    }
}
