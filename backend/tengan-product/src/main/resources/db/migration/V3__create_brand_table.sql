CREATE TABLE brand (
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50)   NOT NULL,
    logo         VARCHAR(255)  NULL,
    descript     VARCHAR(1000) NULL,
    first_letter VARCHAR(1)    NULL,
    sort         INT           NOT NULL DEFAULT 0,
    status       TINYINT       NOT NULL DEFAULT 1 COMMENT '1=SHOW 0=HIDDEN（docs/資料庫設計規範.md：enum存數字）',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
