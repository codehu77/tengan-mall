CREATE TABLE sale_attr_standard_value (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    attr_id     BIGINT UNSIGNED NOT NULL COMMENT '指向 sale_attr.id，驗證在 application 層做',
    label       VARCHAR(50)     NOT NULL COMMENT '聚合值顯示文字，例如「綠」',
    enabled     TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '停用後不再出現在新增/編輯下拉選單，但既有綁定維持顯示',
    sort        INT             NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sale_attr_standard_value_attr_id (attr_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
