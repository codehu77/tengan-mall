CREATE TABLE sku_image (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sku_id     BIGINT UNSIGNED NOT NULL,
    image_url  VARCHAR(255)    NOT NULL,
    sort       INT             NOT NULL DEFAULT 0,
    is_default TINYINT(1)      NOT NULL DEFAULT 0,
    INDEX idx_sku_image_sku_id (sku_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
