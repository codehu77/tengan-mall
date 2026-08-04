-- attr 拆成 base_attr/sale_attr 之後，spu_attr_value 其實一直只存 BASE 類型的實際填值
-- （SALE 的實際填值本來就是存在 sku_sale_attr_value，不是這張表），改名讓語意跟拆分後的
-- 表結構對齊。這張表已經是 SPU/SKU 那條已完成、已驗證過的 migration 鏈的一部分，所以用
-- ALTER TABLE RENAME 新增一支 migration，不直接改寫 V11 那個已經 apply 過的檔案。
ALTER TABLE spu_attr_value RENAME TO spu_base_attr_value;
