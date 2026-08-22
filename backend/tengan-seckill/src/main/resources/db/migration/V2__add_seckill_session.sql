CREATE TABLE seckill_session (
    id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(50)  NOT NULL COMMENT '例如「早場」，純顯示用',
    time_of_day       TIME         NOT NULL COMMENT '每日固定開賣時間點',
    duration_minutes  INT          NOT NULL COMMENT '場次固定時長（分鐘）',
    sort_order        INT          NOT NULL DEFAULT 0,
    enabled           TINYINT      NOT NULL DEFAULT 1 COMMENT '停用後不再出現在新增活動選單，既有活動不受影響',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE seckill_activity
    ADD COLUMN session_id BIGINT UNSIGNED NULL COMMENT 'FLASH_SALE 專用，引用 seckill_session；LAUNCH 為 NULL',
    ADD COLUMN activity_date DATE NULL COMMENT 'FLASH_SALE 專用，場次生效日期；LAUNCH 為 NULL',
    ADD KEY idx_session_date (session_id, activity_date);
