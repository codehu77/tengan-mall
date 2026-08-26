ALTER TABLE sku_launch_config
    ADD COLUMN traffic_gate_enabled TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否啟用開賣流量閘門' AFTER sale_start_time,
    ADD COLUMN gate_close_time     DATETIME NULL COMMENT '閘門關閉時間，之後轉一般 MySQL 路徑' AFTER traffic_gate_enabled,
    ADD COLUMN gate_protected_stock INT      NULL COMMENT 'warm-up 當下快照的保護庫存量，結算時用來算已賣出量' AFTER purchase_limit_per_user,
    ADD COLUMN gate_warmed_at      DATETIME NULL COMMENT '已完成 Redis 預熱的時間戳，避免重複預熱' AFTER gate_protected_stock,
    ADD COLUMN gate_settled_at     DATETIME NULL COMMENT '已完成結算的時間戳，避免重複結算——這三欄(gate_protected_stock/gate_warmed_at/gate_settled_at)是 tengan-inventory 自己的生命週期狀態，product.launch-config.upserted 事件同步時絕對不能覆寫' AFTER gate_warmed_at;
