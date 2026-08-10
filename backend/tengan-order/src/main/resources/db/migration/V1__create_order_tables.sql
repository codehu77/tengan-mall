CREATE TABLE `order` (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_sn        VARCHAR(32)     NOT NULL,
    member_id       BIGINT UNSIGNED NOT NULL,
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '1=PENDING_PAYMENT 2=PAID 3=SHIPPED 4=COMPLETED 5=CANCELLED（docs/資料庫設計規範.md：enum存數字）',
    cancel_reason   VARCHAR(32)     NULL COMMENT 'USER_CANCELLED/TIMEOUT/ADMIN_CANCELLED，只有 CANCELLED 時有值',
    total_amount    DECIMAL(10, 2)  NOT NULL,
    discount_amount DECIMAL(10, 2)  NOT NULL DEFAULT 0,
    pay_amount      DECIMAL(10, 2)  NOT NULL,
    payment_method  VARCHAR(20)     NOT NULL COMMENT 'linepay/credit_card/cod，只記錄意圖，Phase 7 才真的動用',
    coupon_id       BIGINT UNSIGNED NULL COMMENT 'member_coupon.id，業務自然鍵引用，不建物理外鍵',
    receiver_name   VARCHAR(50)     NOT NULL,
    receiver_phone  VARCHAR(20)     NOT NULL,
    city            VARCHAR(50)     NOT NULL,
    district        VARCHAR(50)     NOT NULL,
    postal_code     VARCHAR(10)     NULL,
    street          VARCHAR(200)    NOT NULL,
    remark          VARCHAR(200)    NULL,
    receipt_time    DATETIME        NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_sn (order_sn),
    KEY idx_member_id (member_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 一旦成交的歷史憑證，sku_name/sku_image/price 落地快照，不即時查 tengan-product
-- （見 db_design_conventions「快照 vs 即時查詢判斷準則」）。
CREATE TABLE order_item (
    id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    sku_id   BIGINT UNSIGNED NOT NULL,
    spu_id   BIGINT UNSIGNED NOT NULL,
    sku_name VARCHAR(200)    NOT NULL,
    sku_image VARCHAR(500)   NULL,
    price    DECIMAL(10, 2)  NOT NULL,
    count    INT             NOT NULL,
    subtotal DECIMAL(10, 2)  NOT NULL,
    KEY idx_order_id (order_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 唯讀稽核事實，比照 product_oper_log/inventory_oper_log，只記出貨/客服代取消這兩個稽核寫入操作。
CREATE TABLE order_oper_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operator    VARCHAR(50)  NOT NULL,
    action      VARCHAR(50)  NOT NULL,
    target_desc VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
