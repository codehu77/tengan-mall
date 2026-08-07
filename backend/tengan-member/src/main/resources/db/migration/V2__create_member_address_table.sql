-- 收件地址：獨立聚合根（有自己的 CRUD 生命週期），不建到 member 表的 FK（見資料庫設計規範.md）。
-- address 用單一自由文字欄位，比照 tengan-mall-web 既有 mock 的 receiverAddress 形狀，不拆結構化欄位。
CREATE TABLE member_address (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT UNSIGNED NOT NULL,
    receiver_name  VARCHAR(50)  NOT NULL,
    receiver_phone VARCHAR(20)  NOT NULL,
    address        VARCHAR(255) NOT NULL,
    is_default     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_member_address_member_id (member_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
