CREATE TABLE coupon_template (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100)   NOT NULL,
    threshold_amount DECIMAL(10, 2) NOT NULL,
    discount_amount  DECIMAL(10, 2) NOT NULL,
    total_count      INT            NOT NULL,
    issued_count     INT            NOT NULL DEFAULT 0,
    effective_start  DATETIME       NOT NULL,
    effective_end    DATETIME       NOT NULL,
    status           TINYINT        NOT NULL DEFAULT 1 COMMENT '1=ACTIVE 2=OFF_SHELF（docs/資料庫設計規範.md：enum存數字）',
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
