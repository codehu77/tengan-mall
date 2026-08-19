package com.tengan.mall.payment.application.webhook;

import com.tengan.mall.payment.application.port.WalletPort;
import com.tengan.mall.payment.domain.exception.InvalidCheckMacValueException;
import com.tengan.mall.payment.domain.exception.SubscriptionNotFoundException;
import com.tengan.mall.payment.domain.model.Subscription;
import com.tengan.mall.payment.domain.model.SubscriptionPayment;
import com.tengan.mall.payment.domain.repository.SubscriptionPaymentRepository;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 處理 ECPay `PeriodReturnURL` 通知——第一期跟後續每一期都是同一支處理，沒有特判邏輯。冪等以
 * `gwsr`（ECPay 這期的授權交易單號）為鍵。`RtnCode=1` 才延長 `paidUntil`+升級等級；失敗只累計
 * 連續失敗次數，達到 6 次才標記 `CANCELLED`——不管成功或失敗，都不會在這裡呼叫降級，降級統一交給
 * {@link com.tengan.mall.payment.application.subscription.SubscriptionExpiryScheduler}。
 */
@Service
public class HandlePeriodCallbackService implements HandlePeriodCallbackUseCase {

    private static final Logger log = LoggerFactory.getLogger(HandlePeriodCallbackService.class);
    private static final int MAX_CONSECUTIVE_FAILURES = 6;
    private static final DateTimeFormatter PROCESS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPaymentRepository subscriptionPaymentRepository;
    private final WalletPort walletPort;

    public HandlePeriodCallbackService(EcpayPaymentGatewayClient ecpayPaymentGatewayClient,
            SubscriptionRepository subscriptionRepository, SubscriptionPaymentRepository subscriptionPaymentRepository,
            WalletPort walletPort) {
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPaymentRepository = subscriptionPaymentRepository;
        this.walletPort = walletPort;
    }

    @Override
    @Transactional
    public void handle(HandlePeriodCallbackCommand command) {
        if (!ecpayPaymentGatewayClient.verifyCallback(command.params())) {
            throw new InvalidCheckMacValueException();
        }

        var params = command.params();
        String merchantTradeNo = params.get("MerchantTradeNo");
        String rawGwsr = params.get("gwsr");
        int totalSuccessTimes = parseIntOrZero(params.get("TotalSuccessTimes"));
        boolean success = "1".equals(params.get("RtnCode"));
        BigDecimal amount = parseAmountOrZero(params.get("Amount"));
        Instant processDate = parseProcessDate(params.get("ProcessDate"));
        // gwsr 在失敗通知裡可能缺席，退而求其次用「訂閱編號+第幾次成功+成功與否」組一個穩定的冪等鍵。
        String idempotencyKey = (rawGwsr != null && !rawGwsr.isBlank()) ? rawGwsr
                : merchantTradeNo + ":" + totalSuccessTimes + ":" + success;

        var subscription = subscriptionRepository.findByMerchantTradeNo(merchantTradeNo)
                .orElseThrow(() -> SubscriptionNotFoundException.byMerchantTradeNo(merchantTradeNo));

        applyResult(subscription, idempotencyKey, success, amount, totalSuccessTimes, processDate);
    }

    @Override
    @Transactional
    public void confirmFirstPeriodFromReturnUrl(String merchantTradeNo, String tradeNo, String amountRaw,
            String tradeDateRaw) {
        subscriptionRepository.findByMerchantTradeNo(merchantTradeNo).ifPresent(subscription -> {
            // 跟 gwsr 明確區分開來，避免萬一 ECPay 之後真的又補送同一期的 PeriodReturnURL 時，因為前綴不同
            // 沒被冪等鍵擋下、被誤判成「不同筆通知」而重複入帳——目前 ECPay 測試環境從未觀察到這種重複
            // 送達，這裡先留白名單式的保守作法，不特別再另外查詢是否已有 gwsr 記錄。
            String idempotencyKey = "RETURNURL:" + tradeNo;
            applyResult(subscription, idempotencyKey, true, parseAmountOrZero(amountRaw), 1,
                    parseProcessDate(tradeDateRaw));
        });
    }

    @Override
    @Transactional
    public void confirmFromPeriodQuery(String merchantTradeNo, String gwsr, BigDecimal amount,
            int totalSuccessTimes, Instant processDate) {
        replayPeriodResult(merchantTradeNo, gwsr, true, amount, totalSuccessTimes, processDate);
    }

    @Override
    @Transactional
    public void replayPeriodResult(String merchantTradeNo, String gwsr, boolean success, BigDecimal amount,
            int totalSuccessTimes, Instant processDate) {
        subscriptionRepository.findByMerchantTradeNo(merchantTradeNo).ifPresent(subscription -> {
            String idempotencyKey = (gwsr != null && !gwsr.isBlank()) ? gwsr
                    : merchantTradeNo + ":" + totalSuccessTimes + ":" + success;
            applyResult(subscription, idempotencyKey, success, amount, totalSuccessTimes, processDate);
        });
    }

    /** PeriodReturnURL 的每期通知、跟 ReturnURL 對第一期的即時確認，最終都走同一套入帳邏輯。 */
    private void applyResult(Subscription subscription, String idempotencyKey, boolean success, BigDecimal amount,
            int totalSuccessTimes, Instant processDate) {
        if (subscriptionPaymentRepository.existsByGwsr(idempotencyKey)) {
            log.info("ECPay 定期定額通知重複，冪等略過: key={}", idempotencyKey);
            return;
        }

        subscriptionPaymentRepository.save(SubscriptionPayment.create(subscription.getId(), idempotencyKey, success,
                amount, totalSuccessTimes, processDate));

        if (success) {
            Instant newPaidUntil = ecpayPaymentGatewayClient.nextPaidUntil(processDate);
            subscriptionRepository.extendPaidUntil(subscription.getId(), newPaidUntil);
            try {
                walletPort.upgradeTier(subscription.getMemberId(), subscription.getTargetTier(),
                        "訂閱扣款成功，MerchantTradeNo=" + subscription.getEcpayMerchantTradeNo() + " 第 "
                                + totalSuccessTimes + " 期");
            } catch (RuntimeException e) {
                // 已知邊界情況（比照 HandleEcpayCallbackService 呼叫 tengan-order 失敗的處理方式）：
                // wallet 服務暫時不可用時，不能讓這個例外把上面兩行 rollback 掉——subscription_payment
                // 跟 paid_until 是「ECPay 真的扣到錢了」這個事實的紀錄，就算 wallet 升級失敗也要保留，
                // 不然這筆通知就直接遺失（Controller 那層不管成功失敗都固定回 1|OK，ECPay 不會重試）。
                // 代價是 wallet 那邊可能沒同步到，得靠這行 ERROR log 留人工介入補呼叫。
                log.error("訂閱扣款成功但 wallet 升級失敗，subscriptionId={} targetTier={}，需要人工補呼叫升級",
                        subscription.getId(), subscription.getTargetTier(), e);
            }
        } else {
            int failures = subscriptionRepository.incrementConsecutiveFailures(subscription.getId());
            log.warn("訂閱扣款失敗，merchantTradeNo={} 連續失敗次數={}", subscription.getEcpayMerchantTradeNo(), failures);
            if (failures >= MAX_CONSECUTIVE_FAILURES) {
                subscriptionRepository.markCancelled(subscription.getId());
            }
        }
    }

    private int parseIntOrZero(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private BigDecimal parseAmountOrZero(String value) {
        try {
            return value == null ? BigDecimal.ZERO : new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Instant parseProcessDate(String value) {
        if (value == null || value.isBlank()) {
            return Instant.now();
        }
        try {
            return LocalDateTime.parse(value, PROCESS_DATE_FORMAT).atZone(ZoneId.systemDefault()).toInstant();
        } catch (RuntimeException e) {
            return Instant.now();
        }
    }
}
