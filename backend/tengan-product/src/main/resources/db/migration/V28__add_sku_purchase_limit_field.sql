ALTER TABLE sku
    ADD COLUMN purchase_limit_per_user INT NULL COMMENT '每人限購數量，NULL=不限購';
