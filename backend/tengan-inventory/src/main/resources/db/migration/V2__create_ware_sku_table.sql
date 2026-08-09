CREATE TABLE ware_sku (
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ware_id      BIGINT UNSIGNED NOT NULL,
    sku_id       BIGINT UNSIGNED NOT NULL,
    stock        INT             NOT NULL DEFAULT 0,
    locked_stock INT             NOT NULL DEFAULT 0,
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_ware_sku (ware_id, sku_id),
    KEY idx_sku_id (sku_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
