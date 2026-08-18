package com.tengan.mall.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/** 唯讀日誌：某份訂閱合約其中一期的扣款通知紀錄，寫入後不會被修改。 */
public class SubscriptionPayment {

    private Long id;
    private final Long subscriptionId;
    private final String gwsr;
    private final boolean success;
    private final BigDecimal amount;
    private final int totalSuccessTimes;
    private final Instant processDate;
    private final Instant createdAt;

    private SubscriptionPayment(Long id, Long subscriptionId, String gwsr, boolean success, BigDecimal amount,
            int totalSuccessTimes, Instant processDate, Instant createdAt) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.gwsr = gwsr;
        this.success = success;
        this.amount = amount;
        this.totalSuccessTimes = totalSuccessTimes;
        this.processDate = processDate;
        this.createdAt = createdAt;
    }

    public static SubscriptionPayment create(Long subscriptionId, String gwsr, boolean success, BigDecimal amount,
            int totalSuccessTimes, Instant processDate) {
        return new SubscriptionPayment(null, subscriptionId, gwsr, success, amount, totalSuccessTimes, processDate,
                Instant.now());
    }

    public static SubscriptionPayment reconstitute(Long id, Long subscriptionId, String gwsr, boolean success,
            BigDecimal amount, int totalSuccessTimes, Instant processDate, Instant createdAt) {
        return new SubscriptionPayment(id, subscriptionId, gwsr, success, amount, totalSuccessTimes, processDate,
                createdAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("SubscriptionPayment 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public String getGwsr() {
        return gwsr;
    }

    public boolean isSuccess() {
        return success;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getTotalSuccessTimes() {
        return totalSuccessTimes;
    }

    public Instant getProcessDate() {
        return processDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
