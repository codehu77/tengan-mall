-- 鑑賞期原本設計成「天」為單位，demo 時發現顆粒度太粗（最小非零值就是 1 天，無法調成分鐘級測試）。
-- 改成「分鐘」為單位，現有值乘 1440 保留原本語意（1 天 = 1440 分鐘），後台可以直接改回 1、2 分鐘方便展示。
ALTER TABLE wallet_rule
    CHANGE COLUMN grace_period_days grace_period_minutes INT NOT NULL COMMENT '確認收貨後幾分鐘才正式入帳（鑑賞期，demo 用分鐘顆粒度方便調整）';

UPDATE wallet_rule SET grace_period_minutes = grace_period_minutes * 1440;
