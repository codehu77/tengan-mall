package com.tengan.mall.order.domain.model;

import java.time.Instant;

/**
 * 唯讀操作稽核事實，比照 tengan-inventory 的 InventoryOperLog——只記成功的稽核寫入操作
 * （出貨、後台代客取消），operator 存 X-Identity-Assertion 轉發的 admin JWT username claim。
 * 顧客自己的取消/確認收貨不記在這裡（那是顧客本人的操作，不是「誰動了這筆訂單」的稽核問題）。
 */
public class OrderOperLog {

    private Long id;
    private final String operator;
    private final String action;
    private final String targetDesc;
    private final Instant createdAt;

    private OrderOperLog(Long id, String operator, String action, String targetDesc, Instant createdAt) {
        this.id = id;
        this.operator = operator;
        this.action = action;
        this.targetDesc = targetDesc;
        this.createdAt = createdAt;
    }

    public static OrderOperLog create(String operator, String action, String targetDesc) {
        return new OrderOperLog(null, operator, action, targetDesc, Instant.now());
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("OrderOperLog 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getOperator() {
        return operator;
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
