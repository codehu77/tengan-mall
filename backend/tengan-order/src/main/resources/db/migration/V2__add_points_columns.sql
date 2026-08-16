-- Phase 8：結帳頁點數折抵（比照 coupon_id/discount_amount 的對稱欄位）+ 鑑賞期排程用的追蹤欄位。
ALTER TABLE `order`
    ADD COLUMN points_used            INT            NULL COMMENT '本單使用的點數數量，NULL/0=未使用',
    ADD COLUMN points_discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    ADD COLUMN points_credited        BOOLEAN        NOT NULL DEFAULT FALSE COMMENT 'PointsGrantScheduler 是否已成功呼叫 wallet 入帳';
