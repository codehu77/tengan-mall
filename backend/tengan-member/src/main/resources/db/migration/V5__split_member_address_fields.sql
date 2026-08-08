-- 地址從單一自由文字欄位拆成結構化欄位（城市/區/郵遞區號/街道），配合前台改用城市+區下拉選單的表單設計，
-- 也讓地址列表可以照「街道 / 區, 城市, 郵遞區號」兩行格式顯示。
ALTER TABLE member_address
    ADD COLUMN city        VARCHAR(20)  NOT NULL DEFAULT '' AFTER receiver_phone,
    ADD COLUMN district    VARCHAR(20)  NOT NULL DEFAULT '' AFTER city,
    ADD COLUMN postal_code VARCHAR(10)  NOT NULL DEFAULT '' AFTER district,
    ADD COLUMN street      VARCHAR(255) NOT NULL DEFAULT '' AFTER postal_code;

UPDATE member_address SET street = address;

ALTER TABLE member_address DROP COLUMN address;
