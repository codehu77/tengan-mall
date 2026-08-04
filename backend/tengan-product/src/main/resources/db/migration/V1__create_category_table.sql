CREATE TABLE category (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    parent_id  BIGINT UNSIGNED NOT NULL DEFAULT 0,
    name       VARCHAR(50)     NOT NULL,
    icon       VARCHAR(255)    NULL,
    sort       INT             NOT NULL DEFAULT 0,
    level      TINYINT         NOT NULL,
    status     TINYINT         NOT NULL DEFAULT 1 COMMENT '1=VISIBLE 0=HIDDEN（docs/資料庫設計規範.md：enum存數字）',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_parent_id (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
