-- 唯讀操作稽核事實，比照 tengan-product 的 product_oper_log（同服務內共用一張表，靠 module 區分）。
CREATE TABLE member_oper_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operator    VARCHAR(50)  NOT NULL,
    module      VARCHAR(30)  NOT NULL,
    action      VARCHAR(30)  NOT NULL,
    target_desc VARCHAR(255) NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
