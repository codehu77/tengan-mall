CREATE TABLE seckill_activity (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    activity_type  TINYINT       NOT NULL COMMENT '1=FLASH_SALE(配額稀缺) 2=LAUNCH(流量閘門,配額不一定稀缺)',
    start_time     DATETIME      NOT NULL,
    end_time       DATETIME      NOT NULL COMMENT '兩種類型都必填；LAUNCH 通常是 start_time+30分鐘',
    status         TINYINT       NOT NULL COMMENT '1=DRAFT 2=PUBLISHED 3=ACTIVE 4=SETTLED',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE seckill_sku (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    activity_id     BIGINT UNSIGNED NOT NULL COMMENT '同服務內引用，不建物理外鍵，見 db_design_conventions',
    sku_id          BIGINT UNSIGNED NOT NULL COMMENT 'tengan-product 核發，業務自然鍵，不建物理外鍵',
    seckill_price   DECIMAL(10,2)   NOT NULL,
    seckill_count   INT             NOT NULL COMMENT 'FLASH_SALE=真正配額上限；LAUNCH=節流參考值，實際庫存另查 tengan-inventory',
    limit_per_user  INT             NOT NULL,
    sold_count      INT             NOT NULL DEFAULT 0 COMMENT '結算後回填，settled_at 搭配做冪等判斷',
    settled_at      DATETIME        NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
