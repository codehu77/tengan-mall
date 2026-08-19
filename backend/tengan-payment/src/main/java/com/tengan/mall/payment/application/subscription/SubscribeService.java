package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.application.payment.EcpayFormData;
import com.tengan.mall.payment.domain.exception.AlreadySubscribedException;
import com.tengan.mall.payment.domain.model.Subscription;
import com.tengan.mall.payment.domain.model.SubscriptionStatus;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 建立訂閱合約 + 呼叫 ECPay 定期定額 AioCheckOut 組表單。一個會員同時間只能有一份 ACTIVE 訂閱
 * （見 {@link SubscriptionRepository#findActiveByMemberId}）。`paidUntil` 先保守設成建立當下，
 * 等第一期 `PeriodReturnURL` 成功通知進來才由 webhook 真正延長——這裡不能預先假設第一期一定會成功。
 */
@Service
public class SubscribeService implements SubscribeUseCase {

    private static final DateTimeFormatter MERCHANT_TRADE_NO_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** demo 用的固定定價，PRO/PRO+ 對齊 tengan-wallet 的等級名稱。 */
    private static final BigDecimal PRO_PRICE = new BigDecimal("99");
    private static final BigDecimal PRO_PLUS_PRICE = new BigDecimal("199");

    private final SubscriptionRepository subscriptionRepository;
    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final ReconcileSubscriptionUseCase reconcileSubscriptionUseCase;

    public SubscribeService(SubscriptionRepository subscriptionRepository,
            EcpayPaymentGatewayClient ecpayPaymentGatewayClient,
            ReconcileSubscriptionUseCase reconcileSubscriptionUseCase) {
        this.subscriptionRepository = subscriptionRepository;
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.reconcileSubscriptionUseCase = reconcileSubscriptionUseCase;
    }

    @Override
    @Transactional
    public SubscribeResult subscribe(SubscribeCommand command) {
        subscriptionRepository.findCurrentByMemberId(command.memberId()).ifPresent(existing -> {
            if (existing.getStatus() == SubscriptionStatus.PENDING) {
                // PENDING 是前次嘗試殘留（可能只是使用者放棄結帳，也可能是真的卡住），先同步查 ECPay
                // 真實狀態再決定：查到首期其實成功了就擋（AlreadySubscribedException）；查到未成功
                // reconcile 內部已經 abandonPending 釋放 active_slot，往下可以正常建立新訂閱。
                if (reconcileSubscriptionUseCase.reconcile(existing)) {
                    throw new AlreadySubscribedException(command.memberId());
                }
                return;
            }
            if (existing.getStatus() == SubscriptionStatus.ACTIVE) {
                throw new AlreadySubscribedException(command.memberId());
            }
            // CANCELLED 但還在鑑賞期（benefitExpiredAt 還是 NULL）：這裡不特別擋，active_slot 還沒被
            // markBenefitExpired 清空，下面 INSERT 會撞 uk_active_slot，一樣轉成 AlreadySubscribedException
            // （見下方 catch DuplicateKeyException）。
        });

        String targetTier = command.targetTier().toUpperCase();
        BigDecimal periodAmount = switch (targetTier) {
            case "PRO" -> PRO_PRICE;
            case "PRO_PLUS" -> PRO_PLUS_PRICE;
            default -> throw new IllegalArgumentException("不支援的訂閱等級: " + command.targetTier());
        };

        String merchantTradeNo = generateMerchantTradeNo();
        Subscription subscription = Subscription.create(command.memberId(), targetTier, merchantTradeNo,
                periodAmount);
        try {
            subscriptionRepository.save(subscription);
        } catch (DuplicateKeyException e) {
            // 上面 findCurrentByMemberId 那個檢查中間沒有鎖，兩個併發請求可能都通過檢查、都想 INSERT——
            // uk_active_slot（見 V3 migration）在資料庫層擋下其中一個，這裡轉成跟上面一致的錯誤語意。
            throw new AlreadySubscribedException(command.memberId());
        }

        EcpayFormData form = ecpayPaymentGatewayClient.buildPeriodicCreditCardForm(merchantTradeNo, periodAmount,
                targetTier.equals("PRO") ? "Tengan Mall PRO 會員訂閱" : "Tengan Mall PRO+ 會員訂閱");
        return new SubscribeResult(subscription.getId(), form);
    }

    /** SUB + 14 碼時間戳 + 3 碼亂數 = 20 碼，剛好符合 ECPay MerchantTradeNo ≤20 碼限制。 */
    private String generateMerchantTradeNo() {
        String timestamp = MERCHANT_TRADE_NO_TIMESTAMP.format(LocalDateTime.now());
        int random = ThreadLocalRandom.current().nextInt(1000);
        return "SUB" + timestamp + String.format("%03d", random);
    }
}
