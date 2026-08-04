package com.tengan.mall.admin.domain.model;

import java.time.Instant;

/**
 * 唯讀操作稽核事實——不是有不變條件的聚合根，只有一個工廠方法，沒有變更方法
 * （見 ddd-standards.md 對 Domain Event 的態度：事實一旦發生就不能再改）。
 */
public class OperLog {

    private Long id;
    private final Long adminUserId;
    private final String username;
    private final String module;
    private final String action;
    private final String targetDesc;
    private final int resultStatus;
    private final Instant createdAt;

    private OperLog(Long id, Long adminUserId, String username, String module, String action, String targetDesc,
            int resultStatus, Instant createdAt) {
        this.id = id;
        this.adminUserId = adminUserId;
        this.username = username;
        this.module = module;
        this.action = action;
        this.targetDesc = targetDesc;
        this.resultStatus = resultStatus;
        this.createdAt = createdAt;
    }

    public static OperLog create(Long adminUserId, String username, String module, String action,
            String targetDesc, boolean success) {
        return new OperLog(null, adminUserId, username, module, action, targetDesc, success ? 1 : 0, Instant.now());
    }

    public static OperLog reconstitute(Long id, Long adminUserId, String username, String module, String action,
            String targetDesc, int resultStatus, Instant createdAt) {
        return new OperLog(id, adminUserId, username, module, action, targetDesc, resultStatus, createdAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("OperLog 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public String getUsername() {
        return username;
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

    public int getResultStatus() {
        return resultStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
