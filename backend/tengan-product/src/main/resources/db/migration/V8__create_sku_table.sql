CREATE TABLE sku (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    spu_id     BIGINT UNSIGNED NOT NULL,
    name       VARCHAR(100)    NOT NULL,
    price      DECIMAL(10, 2)  NOT NULL,
    main_image VARCHAR(255)    NULL,
    sale_count INT             NOT NULL DEFAULT 0 COMMENT '遞增邏輯未實作，等 tengan-order 訂單完成後才觸發',
    sort       INT             NOT NULL DEFAULT 0,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sku_spu_id (spu_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
