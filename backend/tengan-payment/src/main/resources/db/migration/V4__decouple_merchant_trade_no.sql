-- Phase 8.6 擴充：修正真實 bug——InitiatePaymentService.initiateCreditCard() 原本直接把 order_sn
-- 當 ECPay MerchantTradeNo 送出，官方硬規定 MerchantTradeNo 為唯一值不可重複使用，使用者信用卡付款
-- 按上一頁重試會拿同一個 order_sn 再送一次 AioCheckOut，直接撞 ECPay 唯一性規則。
--
-- 修法比照 subscription.ecpay_merchant_trade_no 的模式：新增獨立欄位，每次 credit_card 付款嘗試
-- 各自生成一組全新值（"PAY"+14碼時間戳+3碼亂數），不再從 order_sn 推導。唯一索引跟著從 order_sn
-- 移到這個新欄位——order_sn 不再是唯一鍵，因為現在同一張訂單可以留下多筆歷史付款嘗試記錄（重試時
-- 舊記錄轉 status=3 FAILED 存查，不刪除），這批歷史記錄也是之後 Phase 8.6 排程式查帳（掃描卡住太久
-- 的 PENDING 記錄）能撈到候選的資料來源。
ALTER TABLE payment_record
    ADD COLUMN ecpay_merchant_trade_no VARCHAR(20) NULL COMMENT '每次 credit_card 付款嘗試各自一組，linepay/cod 為 NULL' AFTER order_sn;

-- 既有 credit_card 記錄的 order_sn 就是當初實際送給 ECPay 的值，原樣搬過去，讓舊資料也查得到對應的
-- ECPay 交易（雖然理論上撞過唯一性規則的機率很低，這裡只是讓欄位語意一致，不代表這筆一定還查得到）。
UPDATE payment_record SET ecpay_merchant_trade_no = order_sn WHERE method = 'credit_card';

ALTER TABLE payment_record
    DROP INDEX uk_order_sn,
    ADD INDEX idx_order_sn (order_sn),
    ADD UNIQUE KEY uk_ecpay_merchant_trade_no (ecpay_merchant_trade_no),
    MODIFY COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '1=PENDING 2=PAID 3=FAILED（同步查帳確認未付款，結案存查，見 docs/資料庫設計規範.md：enum存數字）';
