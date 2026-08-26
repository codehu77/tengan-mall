ALTER TABLE ware_order_task
    ADD COLUMN member_id BIGINT UNSIGNED NULL COMMENT '下單會員，release 時用來重建 Redis 閘門限購計數 key' AFTER order_sn;

-- ware_id 放寬成可為 NULL：NULL 代表這筆明細是 Redis 閘門保留(gate reservation)，不是真倉鎖定，
-- 見 LockInventoryService 的閘門分支跟 ReleaseInventoryService/DeductInventoryService 的分流邏輯。
ALTER TABLE ware_order_task_detail
    MODIFY COLUMN ware_id BIGINT UNSIGNED NULL COMMENT 'NULL=Redis 閘門保留，非NULL=真倉鎖定';
