package com.tengan.mall.product.domain.model;

import java.time.Instant;

/**
 * 唯讀操作稽核事實——只記成功的寫入操作，不是有不變條件的聚合根，只有一個工廠方法，沒有變更方法
 * （比照 tengan-admin 的 OperLog 設計；這次範圍縮小，沒有 resultStatus 欄位——tengan-product
 * 目前沒有像 tengan-admin AuditingAccessDeniedHandler 那種「連被拒絕的嘗試也要記」的需求，
 * 失敗的請求在 Application Service 拋例外時就直接中斷，不會走到這裡）。
 *
 * <p>operator 存的是 X-Identity-Assertion 轉發的 admin JWT 裡的 username claim（字串），不是
 * tengan-admin 內部的 adminUserId——tengan-product 沒有 admin_user 表可以反查 id 對應的名稱，
 * 直接存人類可讀的名稱，比照 docs/資料庫設計規範.md「跨服務引用鍵用業務自然鍵」的精神。</p>
 */
public class ProductOperLog {

    private Long id;
    private final String operator;
    private final String module;
    private final String action;
    private final String targetDesc;
    private final Instant createdAt;

    private ProductOperLog(Long id, String operator, String module, String action, String targetDesc,
            Instant createdAt) {
        this.id = id;
        this.operator = operator;
        this.module = module;
        this.action = action;
        this.targetDesc = targetDesc;
        this.createdAt = createdAt;
    }

    public static ProductOperLog create(String operator, String module, String action, String targetDesc) {
        return new ProductOperLog(null, operator, module, action, targetDesc, Instant.now());
    }

    public static ProductOperLog reconstitute(Long id, String operator, String module, String action,
            String targetDesc, Instant createdAt) {
        return new ProductOperLog(id, operator, module, action, targetDesc, createdAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ProductOperLog 已經有 id，不可重複指派: " + this.id);
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
