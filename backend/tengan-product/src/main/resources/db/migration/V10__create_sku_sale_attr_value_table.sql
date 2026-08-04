CREATE TABLE sku_sale_attr_value (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sku_id     BIGINT UNSIGNED NOT NULL,
    attr_id    BIGINT UNSIGNED NOT NULL COMMENT '必須指向 attr_type=1(SALE) 的 Attr，驗證在 application 層做',
    attr_name  VARCHAR(50)     NOT NULL COMMENT '冗餘存一份，同 DB 內讀效能考量',
    attr_value VARCHAR(50)     NOT NULL,
    INDEX idx_sku_sale_attr_value_sku_id (sku_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
