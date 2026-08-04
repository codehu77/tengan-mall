CREATE TABLE product_oper_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operator    VARCHAR(50)  NOT NULL COMMENT 'X-Identity-Assertion 轉發的 admin JWT username claim，不是自增 id',
    module      VARCHAR(30)  NOT NULL COMMENT '例如 category/brand/spu',
    action      VARCHAR(30)  NOT NULL COMMENT '例如 create/update/delete',
    target_desc VARCHAR(200) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_oper_log_created_at (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
