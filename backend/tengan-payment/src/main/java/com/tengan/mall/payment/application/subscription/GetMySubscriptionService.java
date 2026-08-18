package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.domain.model.SubscriptionStatus;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class GetMySubscriptionService implements GetMySubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public GetMySubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public MySubscriptionView get(Long memberId) {
        return subscriptionRepository.findCurrentByMemberId(memberId)
                .map(s -> new MySubscriptionView(true, s.getTargetTier(), s.getStatus().name(), s.getPaidUntil(),
                        s.getStatus() == SubscriptionStatus.ACTIVE))
                .orElseGet(MySubscriptionView::none);
    }
}
