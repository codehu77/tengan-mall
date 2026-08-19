package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.application.webhook.HandlePeriodCallbackUseCase;
import com.tengan.mall.payment.domain.model.Subscription;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconcileSubscriptionService implements ReconcileSubscriptionUseCase {

    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final SubscriptionRepository subscriptionRepository;
    private final HandlePeriodCallbackUseCase handlePeriodCallbackUseCase;

    public ReconcileSubscriptionService(EcpayPaymentGatewayClient ecpayPaymentGatewayClient,
            SubscriptionRepository subscriptionRepository, HandlePeriodCallbackUseCase handlePeriodCallbackUseCase) {
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.subscriptionRepository = subscriptionRepository;
        this.handlePeriodCallbackUseCase = handlePeriodCallbackUseCase;
    }

    @Override
    @Transactional
    public boolean reconcile(Subscription pendingSubscription) {
        EcpayPeriodQueryResult queried = ecpayPaymentGatewayClient
                .queryPeriod(pendingSubscription.getEcpayMerchantTradeNo());
        if (queried.firstPeriodSucceeded()) {
            handlePeriodCallbackUseCase.confirmFromPeriodQuery(pendingSubscription.getEcpayMerchantTradeNo(),
                    queried.gwsr(), queried.amount(), queried.totalSuccessTimes(), queried.processDate());
            return true;
        }
        subscriptionRepository.abandonPending(pendingSubscription.getId());
        return false;
    }
}
