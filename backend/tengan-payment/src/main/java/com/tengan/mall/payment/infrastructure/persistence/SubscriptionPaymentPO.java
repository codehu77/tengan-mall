package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("subscription_payment")
public class SubscriptionPaymentPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long subscriptionId;
    private String gwsr;
    private Boolean success;
    private BigDecimal amount;
    private Integer totalSuccessTimes;
    private LocalDateTime processDate;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getGwsr() {
        return gwsr;
    }

    public void setGwsr(String gwsr) {
        this.gwsr = gwsr;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTotalSuccessTimes() {
        return totalSuccessTimes;
    }

    public void setTotalSuccessTimes(Integer totalSuccessTimes) {
        this.totalSuccessTimes = totalSuccessTimes;
    }

    public LocalDateTime getProcessDate() {
        return processDate;
    }

    public void setProcessDate(LocalDateTime processDate) {
        this.processDate = processDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
