-- SPU/SKU 商品管理選單：掛在既有「商品管理」目錄(id=9)底下，比照 Category/Brand/BaseAttr 同一套
-- read/write 兩個權限碼慣例（create/update/delete/publish/unlist 共用同一個 write 權限碼）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (20, 9, 2, 'SPU商品', '/products/spu', 'products/spu/index', 'ProductSpu', 'Goods', 'product:spu:read', 4, 1),
    (21, 20, 3, '新增/編輯SPU', NULL, NULL, NULL, NULL, 'product:spu:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 20), (1, 21);
