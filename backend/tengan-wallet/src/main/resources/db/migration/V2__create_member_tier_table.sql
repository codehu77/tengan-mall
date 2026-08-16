-- lazy-create：查無資料視同 FREE（見 domain/model/MemberTier#defaultFree），不消費 member.registered
-- 事件預建每個會員一筆。Phase 8.5（訂閱）上線前，PRO/PRO+ 只能靠 updated_by/updated_reason 記錄的
-- 後台手動調整（見 UpdateMemberTierService）。
CREATE TABLE member_tier (
    member_id      BIGINT UNSIGNED PRIMARY KEY,
    tier           TINYINT      NOT NULL DEFAULT 1 COMMENT '1=FREE 2=PRO 3=PRO_PLUS',
    updated_by     VARCHAR(50)  NULL,
    updated_reason VARCHAR(200) NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
