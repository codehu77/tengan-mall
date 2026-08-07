-- 唯讀操作稽核事實，比照 tengan-product/tengan-member 的 oper_log 設計。
CREATE TABLE account_oper_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operator    VARCHAR(50)  NOT NULL,
    module      VARCHAR(30)  NOT NULL,
    action      VARCHAR(30)  NOT NULL,
    target_desc VARCHAR(255) NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
