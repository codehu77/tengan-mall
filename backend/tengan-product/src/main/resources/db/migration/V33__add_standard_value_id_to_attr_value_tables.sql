ALTER TABLE spu_base_attr_value
    ADD COLUMN standard_value_id BIGINT UNSIGNED NULL COMMENT '選填，指向 base_attr_standard_value.id，驗證在 application 層做' AFTER attr_value;

ALTER TABLE sku_sale_attr_value
    ADD COLUMN standard_value_id BIGINT UNSIGNED NULL COMMENT '選填，指向 sale_attr_standard_value.id，驗證在 application 層做' AFTER attr_value;
