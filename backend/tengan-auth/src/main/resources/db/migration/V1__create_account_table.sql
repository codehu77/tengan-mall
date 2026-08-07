-- 只存登入需要的最小欄位（微服務前台API待開發清單.md 第2節「開發細節：註冊帳號資料歸屬」）。
-- profile 相關欄位（暱稱、大頭貼、等級）不在這裡，屬於 tengan-member 消費 member.registered 事件後建立。
-- phone/password_hash 允許 NULL：本地密碼註冊必填（application 層擋），第三方登入（Google/FB/LINE，見
-- account_oauth_binding 表）沒有本地密碼、也不一定拿得到手機號碼。email 主要給第三方登入識別用，
-- 也可作為帳號救援/通知的聯絡方式。
CREATE TABLE account (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    phone         VARCHAR(20)  NULL,
    email         VARCHAR(100) NULL,
    password_hash VARCHAR(100) NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=ACTIVE 0=DISABLED（docs/資料庫設計規範.md：enum存數字)',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_account_username (username),
    UNIQUE KEY uk_account_phone (phone),
    UNIQUE KEY uk_account_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
