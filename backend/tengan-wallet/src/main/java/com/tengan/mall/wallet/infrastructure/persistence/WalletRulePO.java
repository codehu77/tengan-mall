package com.tengan.mall.wallet.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

/** 單列設定表，id 固定為 1（見 V1 遷移檔預先塞入的種子資料）。 */
@TableName("wallet_rule")
public class WalletRulePO {

    @TableId
    private Long id;
    private BigDecimal cashbackRatePro;
    private BigDecimal cashbackRateProPlus;
    private Integer monthlyCapPro;
    private Integer monthlyCapProPlus;
    private Integer pointExpiryDays;
    private Integer gracePeriodMinutes;
    private BigDecimal pointValueRatio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCashbackRatePro() {
        return cashbackRatePro;
    }

    public void setCashbackRatePro(BigDecimal cashbackRatePro) {
        this.cashbackRatePro = cashbackRatePro;
    }

    public BigDecimal getCashbackRateProPlus() {
        return cashbackRateProPlus;
    }

    public void setCashbackRateProPlus(BigDecimal cashbackRateProPlus) {
        this.cashbackRateProPlus = cashbackRateProPlus;
    }

    public Integer getMonthlyCapPro() {
        return monthlyCapPro;
    }

    public void setMonthlyCapPro(Integer monthlyCapPro) {
        this.monthlyCapPro = monthlyCapPro;
    }

    public Integer getMonthlyCapProPlus() {
        return monthlyCapProPlus;
    }

    public void setMonthlyCapProPlus(Integer monthlyCapProPlus) {
        this.monthlyCapProPlus = monthlyCapProPlus;
    }

    public Integer getPointExpiryDays() {
        return pointExpiryDays;
    }

    public void setPointExpiryDays(Integer pointExpiryDays) {
        this.pointExpiryDays = pointExpiryDays;
    }

    public Integer getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }

    public void setGracePeriodMinutes(Integer gracePeriodMinutes) {
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

    public BigDecimal getPointValueRatio() {
        return pointValueRatio;
    }

    public void setPointValueRatio(BigDecimal pointValueRatio) {
        this.pointValueRatio = pointValueRatio;
    }
}
