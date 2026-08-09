package com.tengan.mall.coupon.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 聚合根：優惠券模板。折扣規則固定金額滿減（thresholdAmount 門檻 + discountAmount 折抵），
 * 不做百分比折扣、不做限定商品範圍——見開發規劃「設計決策」。
 *
 * <p>issuedCount 的遞增不透過這個聚合根的任何方法——核發是併發熱點，要條件式 UPDATE，不能
 * load-mutate-save（見 CouponTemplateRepository#tryIncrementIssuedCount 的 javadoc）。這裡的
 * issuedCount 只是唯讀快照，供列表頁顯示「已核發/總額度」用。</p>
 */
public class CouponTemplate {

    private Long id;
    private String name;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private int totalCount;
    private final int issuedCount;
    private Instant effectiveStart;
    private Instant effectiveEnd;
    private CouponStatus status;

    private CouponTemplate(Long id, String name, BigDecimal thresholdAmount, BigDecimal discountAmount,
            int totalCount, int issuedCount, Instant effectiveStart, Instant effectiveEnd, CouponStatus status) {
        this.id = id;
        this.name = name;
        this.thresholdAmount = thresholdAmount;
        this.discountAmount = discountAmount;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.effectiveStart = effectiveStart;
        this.effectiveEnd = effectiveEnd;
        this.status = status;
    }

    public static CouponTemplate create(String name, BigDecimal thresholdAmount, BigDecimal discountAmount,
            int totalCount, Instant effectiveStart, Instant effectiveEnd) {
        return new CouponTemplate(null, name, thresholdAmount, discountAmount, totalCount, 0, effectiveStart,
                effectiveEnd, CouponStatus.ACTIVE);
    }

    public static CouponTemplate reconstitute(Long id, String name, BigDecimal thresholdAmount,
            BigDecimal discountAmount, int totalCount, int issuedCount, Instant effectiveStart, Instant effectiveEnd,
            CouponStatus status) {
        return new CouponTemplate(id, name, thresholdAmount, discountAmount, totalCount, issuedCount, effectiveStart,
                effectiveEnd, status);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("CouponTemplate 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void updateRule(String name, BigDecimal thresholdAmount, BigDecimal discountAmount, int totalCount,
            Instant effectiveStart, Instant effectiveEnd) {
        this.name = name;
        this.thresholdAmount = thresholdAmount;
        this.discountAmount = discountAmount;
        this.totalCount = totalCount;
        this.effectiveStart = effectiveStart;
        this.effectiveEnd = effectiveEnd;
    }

    public void delist() {
        this.status = CouponStatus.OFF_SHELF;
    }

    public boolean isUsableFor(BigDecimal amount, Instant now) {
        return status == CouponStatus.ACTIVE && !now.isBefore(effectiveStart) && !now.isAfter(effectiveEnd)
                && amount.compareTo(thresholdAmount) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal amount) {
        return amount.compareTo(thresholdAmount) >= 0 ? discountAmount : BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getThresholdAmount() {
        return thresholdAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getIssuedCount() {
        return issuedCount;
    }

    public Instant getEffectiveStart() {
        return effectiveStart;
    }

    public Instant getEffectiveEnd() {
        return effectiveEnd;
    }

    public CouponStatus getStatus() {
        return status;
    }
}
