-- 庫存流量閘門搬出 SPU，改由 tengan-inventory 自己直接管理（見 sku_launch_config 表）。
-- 這裡刪的是重複、之後也不會再被讀寫的副本欄位，權威資料一直都在 tengan-inventory，不會遺失。
ALTER TABLE spu
    DROP COLUMN traffic_gate_enabled,
    DROP COLUMN gate_close_time;
