CREATE TABLE payment_record (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_sn         VARCHAR(32)  NOT NULL,
    member_id        BIGINT UNSIGNED NOT NULL,
    method           VARCHAR(20)  NOT NULL COMMENT 'linepay/credit_card/cod',
    amount           DECIMAL(10, 2) NOT NULL,
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '1=PENDING 2=PAID（docs/資料庫設計規範.md：enum存數字）',
    gateway_trade_no VARCHAR(64)  NULL COMMENT 'ECPay TradeNo 或 LINE Pay transactionId 二選一，COD 為 NULL',
    paid_at          DATETIME     NULL,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_sn (order_sn),
    UNIQUE KEY uk_gateway_trade_no (gateway_trade_no)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE payment_method_config (
    method     VARCHAR(20) PRIMARY KEY,
    enabled    TINYINT(1)  NOT NULL DEFAULT 1,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

INSERT INTO payment_method_config (method, enabled) VALUES
    ('linepay', 1),
    ('credit_card', 1),
    ('cod', 1);

CREATE TABLE payment_method_oper_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operator    VARCHAR(50)  NOT NULL,
    module      VARCHAR(50)  NOT NULL,
    action      VARCHAR(50)  NOT NULL,
    target_desc VARCHAR(500) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
