-- 媒體生命週期追蹤：上傳當下就寫一筆 PENDING，等擁有者（目前只有 tengan-product 的 SPU/SKU）
-- 用 sync 事件確認引用後才轉 CONFIRMED；沒被確認、或被換掉打回 PENDING 的列，
-- 由 MediaAssetCleanupScheduler 定期回收。object_key 用上傳當下生成的 UUID 命名（見
-- MinioFileStorageAdapter），刻意跟這裡的自增 id 脫鉤——bucket 是匿名公開讀，key 本身是唯一
-- 保護，不能用可枚舉的 id 當物件命名，id 只在服務內部/服務間流動，不會出現在任何對外網址裡。
CREATE TABLE media_asset (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    object_key  VARCHAR(255) NOT NULL,
    url         VARCHAR(500) NOT NULL,
    owner_type  VARCHAR(30)  NULL COMMENT '目前只有 SPU，之後可擴充 BANNER/BRAND_LOGO 等',
    owner_id    BIGINT       NULL,
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1=PENDING(未確認使用) 2=CONFIRMED(使用中)',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_media_asset_object_key (object_key),
    UNIQUE KEY uk_media_asset_url (url),
    INDEX idx_media_asset_owner (owner_type, owner_id),
    INDEX idx_media_asset_status_updated (status, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
