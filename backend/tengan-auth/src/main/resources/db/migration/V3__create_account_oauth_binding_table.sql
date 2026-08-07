-- 第三方登入（Google/FB/LINE）綁定關係，一個 account 可以同時綁多個 provider。
-- 純關聯查詢表，不是獨立聚合根（比照 role_menu 這類 join table 的既有處理方式）。
CREATE TABLE account_oauth_binding (
    id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    account_id        BIGINT UNSIGNED NOT NULL,
    provider          TINYINT      NOT NULL COMMENT '1=GOOGLE 2=FACEBOOK 3=LINE',
    provider_user_id  VARCHAR(100) NOT NULL COMMENT '第三方系統的使用者唯一識別（如 Google sub claim）',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_provider_user (provider, provider_user_id),
    UNIQUE KEY uk_account_provider (account_id, provider)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
