package com.tengan.mall.wallet.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tengan.mall.wallet.domain.model.PointsTransactionStatus;
import com.tengan.mall.wallet.domain.model.PointsTransactionType;
import java.time.LocalDateTime;

@TableName("points_transaction")
public class PointsTransactionPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private PointsTransactionType type;
    private PointsTransactionStatus status;
    private Integer points;
    private Integer balanceAfter;
    private String title;
    private String description;
    private String orderSn;
    private String channel;
    private String operator;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long relatedTransactionId;

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

    public PointsTransactionType getType() {
        return type;
    }

    public void setType(PointsTransactionType type) {
        this.type = type;
    }

    public PointsTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(PointsTransactionStatus status) {
        this.status = status;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Integer balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getRelatedTransactionId() {
        return relatedTransactionId;
    }

    public void setRelatedTransactionId(Long relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }
}
