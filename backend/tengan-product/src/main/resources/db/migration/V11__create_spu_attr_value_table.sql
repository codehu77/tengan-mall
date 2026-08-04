CREATE TABLE spu_attr_value (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    spu_id     BIGINT UNSIGNED NOT NULL,
    attr_id    BIGINT UNSIGNED NOT NULL COMMENT '必須指向 attr_type=0(BASE) 的 Attr，驗證在 application 層做',
    attr_name  VARCHAR(50)     NOT NULL,
    attr_value VARCHAR(50)     NOT NULL,
    INDEX idx_spu_attr_value_spu_id (spu_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
