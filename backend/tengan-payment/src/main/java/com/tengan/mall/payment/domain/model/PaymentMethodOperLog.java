package com.tengan.mall.payment.domain.model;

import java.time.Instant;

/** 唯讀操作稽核事實，只記管理員動作（比照 CouponOperLog/InventoryOperLog 全站既有慣例）。 */
public class PaymentMethodOperLog {

    private Long id;
    private final String operator;
    private final String module;
    private final String action;
    private final String targetDesc;
    private final Instant createdAt;

    private PaymentMethodOperLog(Long id, String operator, String module, String action, String targetDesc,
            Instant createdAt) {
        this.id = id;
        this.operator = operator;
        this.module = module;
        this.action = action;
        this.targetDesc = targetDesc;
        this.createdAt = createdAt;
    }

    public static PaymentMethodOperLog create(String operator, String module, String action, String targetDesc) {
        return new PaymentMethodOperLog(null, operator, module, action, targetDesc, Instant.now());
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("PaymentMethodOperLog 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getOperator() {
        return operator;
    }

    public String getModule() {
        return module;
    }

    public String getAction() {
        return action;
    }

    public String getTargetDesc() {
        return targetDesc;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
