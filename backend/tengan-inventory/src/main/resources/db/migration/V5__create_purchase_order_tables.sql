CREATE TABLE purchase_order (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    po_number     VARCHAR(32)     NOT NULL,
    ware_id       BIGINT UNSIGNED NOT NULL,
    supplier_name VARCHAR(100)    NULL,
    status        TINYINT         NOT NULL DEFAULT 1 COMMENT '1=PENDING 2=RECEIVED（docs/資料庫設計規範.md：enum存數字）',
    created_by    VARCHAR(50)     NOT NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    received_at   DATETIME        NULL,
    UNIQUE KEY uk_po_number (po_number)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE purchase_order_item (
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    po_id        BIGINT UNSIGNED NOT NULL,
    sku_id       BIGINT UNSIGNED NOT NULL,
    ordered_qty  INT             NOT NULL,
    received_qty INT             NULL,
    KEY idx_po_id (po_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
