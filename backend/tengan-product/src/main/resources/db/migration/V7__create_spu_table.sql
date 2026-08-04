CREATE TABLE spu (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT UNSIGNED NOT NULL COMMENT '只能指向 level=3（葉節點）的分類',
    brand_id    BIGINT UNSIGNED NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    description TEXT            NULL,
    main_image  VARCHAR(255)    NULL,
    status      TINYINT         NOT NULL DEFAULT 0 COMMENT '0=NEW 1=ON_SHELF 2=OFF_SHELF（docs/資料庫設計規範.md：enum存數字）',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_spu_category_id (category_id),
    INDEX idx_spu_brand_id (brand_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
