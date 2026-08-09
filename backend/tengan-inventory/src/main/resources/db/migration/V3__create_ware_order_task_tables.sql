CREATE TABLE ware_order_task (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_sn   VARCHAR(64) NOT NULL,
    status     TINYINT     NOT NULL DEFAULT 1 COMMENT '1=LOCKED 2=RELEASED 3=DEDUCTED（docs/資料庫設計規範.md：enum存數字）',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_sn (order_sn)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE ware_order_task_detail (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    task_id    BIGINT UNSIGNED NOT NULL,
    ware_id    BIGINT UNSIGNED NOT NULL,
    sku_id     BIGINT UNSIGNED NOT NULL,
    sku_count  INT             NOT NULL,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_task_id (task_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
