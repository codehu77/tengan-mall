-- 會員購物車：永久保留，不設 TTL（訪客購物車走 Redis，見 cart_storage_decision）。
-- 不存 sku 價格快照，讀取時即時查 tengan-product 目前的價格。
CREATE TABLE cart_item (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT UNSIGNED NOT NULL,
    sku_id      BIGINT UNSIGNED NOT NULL,
    count       INT UNSIGNED    NOT NULL,
    checked     TINYINT(1)      NOT NULL DEFAULT 1,
    spec_text   VARCHAR(255)             DEFAULT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_cart_item_user_sku (user_id, sku_id),
    KEY idx_cart_item_user_id (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
