-- Spu 層級共通圖片（所有底下的 Sku 共用，例如整體外觀/包裝/尺寸表），跟 sku_image 分開存。
-- 沒有 is_default 欄位——比照 V21 把 sku_image.is_default 拿掉的理由，主圖已經有 spu.main_image
-- 這個單獨欄位負責，這張表純粹是額外的圖片清單。
CREATE TABLE spu_image (
    id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    spu_id    BIGINT UNSIGNED NOT NULL,
    image_url VARCHAR(255)    NOT NULL,
    sort      INT             NOT NULL DEFAULT 0,
    INDEX idx_spu_image_spu_id (spu_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
