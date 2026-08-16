-- 單列設定表（全專案第一次出現這種表），id 固定為 1。grace_period_days/point_expiry_days 刻意設計成
-- 可後台即時修改的 DB 欄位而非 Nacos 靜態設定——demo 時把鑑賞期調短不需要重啟服務（見 .docs 開發順序清單
-- Phase 8「Demo 策略」）。
CREATE TABLE wallet_rule (
    id                     BIGINT UNSIGNED PRIMARY KEY,
    cashback_rate_pro      DECIMAL(4, 3)   NOT NULL COMMENT 'PRO 消費回饋比例，例如 0.020 = 2%',
    cashback_rate_pro_plus DECIMAL(4, 3)   NOT NULL COMMENT 'PRO+ 消費回饋比例，例如 0.040 = 4%',
    monthly_cap_pro        INT             NULL COMMENT 'PRO 單月入帳上限，NULL=無上限',
    monthly_cap_pro_plus   INT             NULL COMMENT 'PRO+ 單月入帳上限，NULL=無上限（預設無上限）',
    point_expiry_days      INT             NOT NULL COMMENT '點數到期天數',
    grace_period_days      INT             NOT NULL COMMENT '確認收貨後幾天才正式入帳（鑑賞期）',
    point_value_ratio      DECIMAL(6, 3)   NOT NULL COMMENT '1 點兌換多少台幣，例如 1.000 = 1點=NT$1'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

INSERT INTO wallet_rule (id, cashback_rate_pro, cashback_rate_pro_plus, monthly_cap_pro, monthly_cap_pro_plus,
                          point_expiry_days, grace_period_days, point_value_ratio)
VALUES (1, 0.020, 0.040, 500, NULL, 365, 7, 1.000);
