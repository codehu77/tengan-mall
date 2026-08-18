package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tengan.mall.payment.domain.model.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("subscription")
public class SubscriptionPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    /** 現在算數時=memberId，已結束時=null，供 uk_active_slot 擋併發重複訂閱，純基礎設施欄位不進 domain model。 */
    private Long activeSlot;
    private String targetTier;
    private SubscriptionStatus status;
    private String ecpayMerchantTradeNo;
    private BigDecimal periodAmount;
    private Integer consecutiveFailures;
    private LocalDateTime paidUntil;
    private LocalDateTime benefitExpiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(Long activeSlot) {
        this.activeSlot = activeSlot;
    }

    public String getTargetTier() {
        return targetTier;
    }

    public void setTargetTier(String targetTier) {
        this.targetTier = targetTier;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getEcpayMerchantTradeNo() {
        return ecpayMerchantTradeNo;
    }

    public void setEcpayMerchantTradeNo(String ecpayMerchantTradeNo) {
        this.ecpayMerchantTradeNo = ecpayMerchantTradeNo;
    }

    public BigDecimal getPeriodAmount() {
        return periodAmount;
    }

    public void setPeriodAmount(BigDecimal periodAmount) {
        this.periodAmount = periodAmount;
    }

    public Integer getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void setConsecutiveFailures(Integer consecutiveFailures) {
        this.consecutiveFailures = consecutiveFailures;
    }

    public LocalDateTime getPaidUntil() {
        return paidUntil;
    }

    public void setPaidUntil(LocalDateTime paidUntil) {
        this.paidUntil = paidUntil;
    }

    public LocalDateTime getBenefitExpiredAt() {
        return benefitExpiredAt;
    }

    public void setBenefitExpiredAt(LocalDateTime benefitExpiredAt) {
        this.benefitExpiredAt = benefitExpiredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
