-- 運費機制：order 表補運費欄位（併入 pay_amount 一起計算），另建單列設定表（比照
-- tengan-wallet 的 wallet_rule，id 固定為 1）存免運門檻/運費金額，後台可即時調整、不需重啟服務。
ALTER TABLE `order`
    ADD COLUMN shipping_fee DECIMAL(10, 2) NOT NULL DEFAULT 0 AFTER pay_amount;

CREATE TABLE order_freight_rule (
    id                       BIGINT UNSIGNED PRIMARY KEY,
    free_shipping_threshold DECIMAL(10, 2) NOT NULL COMMENT '商品原價小計達此金額免運',
    shipping_fee             DECIMAL(10, 2) NOT NULL COMMENT '未達門檻時收取的運費'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

INSERT INTO order_freight_rule (id, free_shipping_threshold, shipping_fee)
VALUES (1, 600.00, 75.00);
