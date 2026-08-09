package com.tengan.mall.coupon.domain.model;

import java.time.Instant;

/** 唯讀操作稽核事實，比照 tengan-product 的 ProductOperLog——只記成功的寫入操作。 */
public class CouponOperLog {

    private Long id;
    private final String operator;
    private final String module;
    private final String action;
    private final String targetDesc;
    private final Instant createdAt;

    private CouponOperLog(Long id, String operator, String module, String action, String targetDesc,
            Instant createdAt) {
        this.id = id;
        this.operator = operator;
        this.module = module;
        this.action = action;
        this.targetDesc = targetDesc;
        this.createdAt = createdAt;
    }

    public static CouponOperLog create(String operator, String module, String action, String targetDesc) {
        return new CouponOperLog(null, operator, module, action, targetDesc, Instant.now());
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("CouponOperLog 已經有 id，不可重複指派: " + this.id);
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
