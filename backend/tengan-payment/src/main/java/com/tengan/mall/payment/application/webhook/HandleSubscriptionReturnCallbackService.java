package com.tengan.mall.payment.application.webhook;

import com.tengan.mall.payment.domain.exception.InvalidCheckMacValueException;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ECPay 定期定額訂閱專屬的 ReturnURL 通知——跟一般訂單的 {@link HandleEcpayCallbackService} 是完全
 * 不同的 use case，故意分開一支獨立的端點/設定值（{@code ecpay-subscription-return-url}），不共用
 * 一般訂單那支再靠「查不到 payment_record」反推是訂閱單。
 *
 * <p>這是 ECPay AioCheckOut 當下同步送回的首刷授權結果，比要等批次送達、可能延遲到隔天的
 * {@code PeriodReturnURL} 快很多，所以訂閱的「第一期」改用這個管道確認，讓使用者訂閱當下就能拿到
 * PRO 權益，不用乾等。第二期以後沒有這個同步管道，仍然只能靠 {@link HandlePeriodCallbackService}
 * 處理 {@code PeriodReturnURL}。</p>
 */
@Service
public class HandleSubscriptionReturnCallbackService implements HandleSubscriptionReturnCallbackUseCase {

    private static final Logger log = LoggerFactory.getLogger(HandleSubscriptionReturnCallbackService.class);

    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final HandlePeriodCallbackUseCase handlePeriodCallbackUseCase;
    private final SubscriptionRepository subscriptionRepository;

    public HandleSubscriptionReturnCallbackService(EcpayPaymentGatewayClient ecpayPaymentGatewayClient,
            HandlePeriodCallbackUseCase handlePeriodCallbackUseCase, SubscriptionRepository subscriptionRepository) {
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.handlePeriodCallbackUseCase = handlePeriodCallbackUseCase;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    @Transactional
    public void handle(HandleSubscriptionReturnCallbackCommand command) {
        if (!ecpayPaymentGatewayClient.verifyCallback(command.params())) {
            throw new InvalidCheckMacValueException();
        }

        var params = command.params();
        String merchantTradeNo = params.get("MerchantTradeNo");
        String rtnCode = params.get("RtnCode");
        if (!"1".equals(rtnCode)) {
            log.warn("ECPay 訂閱首刷授權未成功: merchantTradeNo={} rtnCode={}", merchantTradeNo, rtnCode);
            // 首刷沒過，這份訂閱從沒真的生效過（還停在 PENDING），直接轉 CANCELLED，而不是放著卡住——
            // SubscriptionExpiryScheduler 只處理 CANCELLED，放著不管的話 findCurrentByMemberId 會一直
            // 查到這筆從沒成功過的訂閱，導致這個會員永遠無法重新訂閱。

            subscriptionRepository.findByMerchantTradeNo(merchantTradeNo)
                    .ifPresent(subscription -> subscriptionRepository.markCancelled(subscription.getId()));
            return;
        }

        handlePeriodCallbackUseCase.confirmFirstPeriodFromReturnUrl(merchantTradeNo, params.get("TradeNo"),
                params.get("TradeAmt"), params.get("TradeDate"));
    }
}
