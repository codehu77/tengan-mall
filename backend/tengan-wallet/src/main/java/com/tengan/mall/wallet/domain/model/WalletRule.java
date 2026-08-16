package com.tengan.mall.wallet.domain.model;

import java.math.BigDecimal;

/**
 * 單列設定表（全專案第一次出現「single-row config」這種表，id 固定為 1）。`monthlyCapPro`/
 * `monthlyCapProPlus` 為 null 代表無上限（PRO+ 預設無上限）。`gracePeriodMinutes`/`pointExpiryDays`
 * 刻意設計成可後台即時修改的 DB 欄位而非 Nacos 靜態設定——demo 時要把鑑賞期/到期天數調成方便展示的
 * 短值，不需要重啟服務。`gracePeriodMinutes` 原本設計成「天」為單位，demo 時發現顆粒度太粗（最小非零值
 * 就是 1 天），V4 migration 改成「分鐘」為單位。
 */
public class WalletRule {

    private final BigDecimal cashbackRatePro;
    private final BigDecimal cashbackRateProPlus;
    private final Integer monthlyCapPro;
    private final Integer monthlyCapProPlus;
    private final int pointExpiryDays;
    private final int gracePeriodMinutes;
    private final BigDecimal pointValueRatio;

    public WalletRule(BigDecimal cashbackRatePro, BigDecimal cashbackRateProPlus, Integer monthlyCapPro,
            Integer monthlyCapProPlus, int pointExpiryDays, int gracePeriodMinutes, BigDecimal pointValueRatio) {
        this.cashbackRatePro = cashbackRatePro;
        this.cashbackRateProPlus = cashbackRateProPlus;
        this.monthlyCapPro = monthlyCapPro;
        this.monthlyCapProPlus = monthlyCapProPlus;
        this.pointExpiryDays = pointExpiryDays;
        this.gracePeriodMinutes = gracePeriodMinutes;
        this.pointValueRatio = pointValueRatio;
    }

    public BigDecimal cashbackRateFor(MemberTierLevel tier) {
        return switch (tier) {
            case PRO -> cashbackRatePro;
            case PRO_PLUS -> cashbackRateProPlus;
            case FREE -> BigDecimal.ZERO;
        };
    }

    public Integer monthlyCapFor(MemberTierLevel tier) {
        return switch (tier) {
            case PRO -> monthlyCapPro;
            case PRO_PLUS -> monthlyCapProPlus;
            case FREE -> 0;
        };
    }

    public BigDecimal getCashbackRatePro() {
        return cashbackRatePro;
    }

    public BigDecimal getCashbackRateProPlus() {
        return cashbackRateProPlus;
    }

    public Integer getMonthlyCapPro() {
        return monthlyCapPro;
    }

    public Integer getMonthlyCapProPlus() {
        return monthlyCapProPlus;
    }

    public int getPointExpiryDays() {
        return pointExpiryDays;
    }

    public int getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }

    public BigDecimal getPointValueRatio() {
        return pointValueRatio;
    }
}
