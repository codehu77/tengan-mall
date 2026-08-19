package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconcilePaymentService implements ReconcilePaymentUseCase {

    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final PaymentRecordRepository paymentRecordRepository;
    private final PaymentConvergenceService paymentConvergenceService;

    public ReconcilePaymentService(EcpayPaymentGatewayClient ecpayPaymentGatewayClient,
            PaymentRecordRepository paymentRecordRepository, PaymentConvergenceService paymentConvergenceService) {
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.paymentRecordRepository = paymentRecordRepository;
        this.paymentConvergenceService = paymentConvergenceService;
    }

    @Override
    @Transactional
    public boolean reconcile(PaymentRecord pendingCreditCardRecord) {
        EcpayTradeQueryResult queried = ecpayPaymentGatewayClient
                .queryTrade(pendingCreditCardRecord.getEcpayMerchantTradeNo());
        if (queried.isPaid()) {
            paymentConvergenceService.convergeToPaid(pendingCreditCardRecord, queried.tradeNo());
            return true;
        }
        paymentRecordRepository.markFailed(pendingCreditCardRecord.getId());
        return false;
    }
}
