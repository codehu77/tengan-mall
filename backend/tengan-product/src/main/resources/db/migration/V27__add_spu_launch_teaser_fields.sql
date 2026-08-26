ALTER TABLE spu
    ADD COLUMN sale_start_time       DATETIME     NULL COMMENT '開賣時間，NULL=上架後立即可購買',
    ADD COLUMN traffic_gate_enabled  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否啟用開賣流量閘門保護（Phase A 只存欄位，實際 Redis 保護機制留待 Phase B）',
    ADD COLUMN gate_close_time       DATETIME     NULL COMMENT '流量閘門關閉時間，須晚於 sale_start_time',
    ADD COLUMN show_on_launch_teaser TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否顯示於首頁「即將開賣」預告區塊',
    ADD COLUMN teaser_remove_at      DATETIME     NULL COMMENT '從首頁預告區塊下架的時間，須晚於 sale_start_time';
