CREATE TABLE coupon_oper_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operator    VARCHAR(50)   NOT NULL,
    module      VARCHAR(50)   NOT NULL,
    action      VARCHAR(50)   NOT NULL,
    target_desc VARCHAR(500)  NOT NULL,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
