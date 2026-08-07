package com.tengan.mall.auth.domain.model;

import java.time.Instant;

/**
 * 唯讀操作稽核事實，比照 tengan-product/tengan-member 的 oper_log 設計——這是 tengan-auth
 * 第一次有管理端觸發的寫入端點（disable/enable account），之前 register/login 都是使用者自己
 * 觸發，不需要稽核。operator 存 X-Identity-Assertion 轉發的 admin JWT username claim。
 */
public class AccountOperLog {

    private Long id;
    private final String operator;
    private final String module;
    private final String action;
    private final String targetDesc;
    private final Instant createdAt;

    private AccountOperLog(Long id, String operator, String module, String action, String targetDesc,
            Instant createdAt) {
        this.id = id;
        this.operator = operator;
        this.module = module;
        this.action = action;
        this.targetDesc = targetDesc;
        this.createdAt = createdAt;
    }

    public static AccountOperLog create(String operator, String module, String action, String targetDesc) {
        return new AccountOperLog(null, operator, module, action, targetDesc, Instant.now());
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("AccountOperLog 已經有 id，不可重複指派: " + this.id);
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
