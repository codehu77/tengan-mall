-- 防超賣保護入口搬回 SPU 列表頁，需要用 spu_id 反查一批 SPU 的代表性保護狀態，不然 SPU 列表頁每列都要
-- 呼叫一次 getSpu(spuId) 才能拿到 skuId 清單再反查，N+1。ProductLaunchConfigChangedEvent 本來就带
-- spuId，這裡把它一起存下來即可。
ALTER TABLE sku_launch_config
    ADD COLUMN spu_id BIGINT NULL COMMENT '所屬 SPU id，供 SPU 列表頁批次反查保護狀態用' AFTER sku_id,
    ADD INDEX idx_spu_id (spu_id);
