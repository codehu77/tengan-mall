CREATE TABLE sku_launch_config (
    sku_id                  BIGINT UNSIGNED PRIMARY KEY,
    sale_start_time         DATETIME NULL COMMENT '開賣時間，NULL=已可直接購買',
    purchase_limit_per_user INT      NULL COMMENT '每人限購數量，NULL=不限購',
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci
  COMMENT = '從 tengan-product 單向同步過來的本地副本（訂閱 product.launch-config.upserted 事件），下單鎖庫存時用來檢查開賣時間/限購。Phase A 只同步這兩個欄位，traffic_gate_enabled/gate_close_time 留給 Phase B 的 Redis 流量閘門機制再補';
