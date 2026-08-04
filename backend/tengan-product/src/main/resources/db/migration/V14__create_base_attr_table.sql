CREATE TABLE base_attr (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id   BIGINT UNSIGNED NOT NULL COMMENT '只能指向 level=3（葉節點）的分類，驗證在 application 層做',
    attr_group_id BIGINT UNSIGNED NOT NULL COMMENT '一定要歸屬一個 base_attr_group，跟 BASE/SALE 拆表前不同：這裡是 NOT NULL',
    name          VARCHAR(50)     NOT NULL,
    searchable    TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否加入搜尋篩選面向',
    sort          INT             NOT NULL DEFAULT 0,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_base_attr_category_id (category_id),
    INDEX idx_base_attr_attr_group_id (attr_group_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
