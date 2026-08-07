-- id 沿用 tengan-auth account 表的 accountId（消費 member.registered 事件時原樣帶入，不是自增）。
-- phone 允許 NULL：account.phone 現在也是 nullable（見 tengan-auth 的 OAuth 準備調整），這裡
-- 只是快照，不是這個服務的登入憑證。
CREATE TABLE member (
    id          BIGINT UNSIGNED PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    phone       VARCHAR(20)  NULL,
    nickname    VARCHAR(50)  NOT NULL,
    avatar_url  VARCHAR(255) NULL,
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1=ACTIVE 0=BANNED',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
