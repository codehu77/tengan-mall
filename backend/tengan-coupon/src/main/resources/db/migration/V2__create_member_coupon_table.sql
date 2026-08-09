CREATE TABLE member_coupon (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT UNSIGNED NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    use_status  TINYINT         NOT NULL DEFAULT 1 COMMENT '1=UNUSED 2=USED（docs/資料庫設計規範.md：enum存數字）',
    order_sn    VARCHAR(64)     NULL,
    received_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_template_id (template_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
