-- Phase 8.5：訂閱式付款。subscription 是一份持續扣款的合約，跟 payment_record（一張訂單對應一筆付款，
-- UNIQUE KEY uk_order_sn）性質不同，不硬塞進 payment_record。
--
-- status(ACTIVE/CANCELLED) 只代表「ECPay 還會不會繼續扣款」，跟「會員現在有沒有 PRO 權益」是兩件事：
-- paid_until 才是「現在算不算 PRO」的唯一權威依據，每次扣款成功往後延一期。取消訂閱（不管顧客主動
-- 取消還是連續失敗 6 次被系統停用）都只改 status/cancelled_at，不動 paid_until、不降級——使用者已經
-- 付過的那一期還沒到期。降級這件事統一交給 SubscriptionExpiryScheduler 排程在 paid_until 到期時處理，
-- 不在取消/失敗的當下同步執行。
CREATE TABLE subscription (
    id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id             BIGINT UNSIGNED NOT NULL,
    target_tier           VARCHAR(20)     NOT NULL COMMENT 'PRO/PRO_PLUS 字串（對齊 tengan-wallet MemberTierLevel 名稱，不用數字避免跨服務耦合到對方的 enum code）',
    status                TINYINT         NOT NULL DEFAULT 1 COMMENT '1=ACTIVE 2=CANCELLED，只代表還會不會繼續扣款',
    ecpay_merchant_trade_no VARCHAR(20)   NOT NULL COMMENT '訂閱建立時的 MerchantTradeNo，之後每期 PeriodReturnURL 通知靠這個關聯回這張訂閱',
    period_amount         DECIMAL(10, 2)  NOT NULL,
    consecutive_failures  INT             NOT NULL DEFAULT 0,
    paid_until            DATETIME        NOT NULL COMMENT '已付費到期日，扣款成功才會延長，是否為 PRO 的唯一權威依據',
    benefit_expired_at    DATETIME        NULL COMMENT 'SubscriptionExpiryScheduler 降級後標記，冪等防重複降級',
    created_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at          DATETIME        NULL COMMENT '停止續訂的時間點，不等於權益到期時間點',
    updated_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_ecpay_merchant_trade_no (ecpay_merchant_trade_no),
    KEY idx_member_id (member_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 唯讀日誌，每一期 PeriodReturnURL 通知都新增一筆，不會被修改。gwsr 是 ECPay 這期的授權交易單號，
-- UNIQUE 供冪等（同一筆通知重送不會被處理兩次）。
CREATE TABLE subscription_payment (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    subscription_id     BIGINT UNSIGNED NOT NULL,
    gwsr                VARCHAR(30)     NOT NULL COMMENT 'ECPay 這期的授權交易單號',
    success             BOOLEAN         NOT NULL,
    amount              DECIMAL(10, 2)  NOT NULL,
    total_success_times INT             NOT NULL COMMENT 'ECPay 回傳的目前累積成功次數',
    process_date        DATETIME        NOT NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gwsr (gwsr),
    KEY idx_subscription_id (subscription_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
