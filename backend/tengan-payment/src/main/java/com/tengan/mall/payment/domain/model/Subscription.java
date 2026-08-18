package com.tengan.mall.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 聚合根：一份持續扣款的訂閱合約。`status`（PENDING/ACTIVE/CANCELLED）只代表「ECPay 還會不會繼續扣款」，
 * 跟「會員現在有沒有 PRO 權益」是兩件事——後者的唯一權威依據是 `paidUntil`（已付費到期日），
 * 每次扣款成功才會往後延一期。取消訂閱（不管顧客主動取消還是連續失敗 6 次被系統停用）都只轉
 * `status=CANCELLED` + 記 `cancelledAt`，完全不動 `paidUntil`、也不降級——使用者已經付過的那一期
 * 還沒到期。降級這件事統一交給 `SubscriptionExpiryScheduler` 排程在 `paidUntil` 到期時才處理。
 *
 * <p>狀態轉換（延長 paidUntil/標記 CANCELLED/標記 benefitExpiredAt）一律走
 * {@link com.tengan.mall.payment.domain.repository.SubscriptionRepository} 的條件式 UPDATE，
 * 不透過這裡的 setter/mutate 方法（比照全站既有的 Order/PaymentRecord 模式）。</p>
 */
public class Subscription {

    private Long id;
    private final Long memberId;
    private final String targetTier;
    private final SubscriptionStatus status;
    private final String ecpayMerchantTradeNo;
    private final BigDecimal periodAmount;
    private final int consecutiveFailures;
    private final Instant paidUntil;
    private final Instant benefitExpiredAt;
    private final Instant createdAt;
    private final Instant cancelledAt;

    private Subscription(Long id, Long memberId, String targetTier, SubscriptionStatus status,
            String ecpayMerchantTradeNo, BigDecimal periodAmount, int consecutiveFailures, Instant paidUntil,
            Instant benefitExpiredAt, Instant createdAt, Instant cancelledAt) {
        this.id = id;
        this.memberId = memberId;
        this.targetTier = targetTier;
        this.status = status;
        this.ecpayMerchantTradeNo = ecpayMerchantTradeNo;
        this.periodAmount = periodAmount;
        this.consecutiveFailures = consecutiveFailures;
        this.paidUntil = paidUntil;
        this.benefitExpiredAt = benefitExpiredAt;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    /**
     * status 從 PENDING 開始——使用者這時候連信用卡都還沒刷，還不算真正的訂閱，等首刷 ReturnURL
     * 通知回來才轉 ACTIVE（成功）或 CANCELLED（失敗）。paidUntil 先保守設成建立當下，之後才由
     * webhook 真正延長。
     */
    public static Subscription create(Long memberId, String targetTier, String ecpayMerchantTradeNo,
            BigDecimal periodAmount) {
        Instant now = Instant.now();
        return new Subscription(null, memberId, targetTier, SubscriptionStatus.PENDING, ecpayMerchantTradeNo,
                periodAmount, 0, now, null, now, null);
    }

    public static Subscription reconstitute(Long id, Long memberId, String targetTier, SubscriptionStatus status,
            String ecpayMerchantTradeNo, BigDecimal periodAmount, int consecutiveFailures, Instant paidUntil,
            Instant benefitExpiredAt, Instant createdAt, Instant cancelledAt) {
        return new Subscription(id, memberId, targetTier, status, ecpayMerchantTradeNo, periodAmount,
                consecutiveFailures, paidUntil, benefitExpiredAt, createdAt, cancelledAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Subscription 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getTargetTier() {
        return targetTier;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public String getEcpayMerchantTradeNo() {
        return ecpayMerchantTradeNo;
    }

    public BigDecimal getPeriodAmount() {
        return periodAmount;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public Instant getPaidUntil() {
        return paidUntil;
    }

    public Instant getBenefitExpiredAt() {
        return benefitExpiredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }
}
