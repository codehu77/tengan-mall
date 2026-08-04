-- 規格屬性頁面選單：BASE/SALE 拆成兩個聚合根之後的新設計，取代舊版（已刪除）的 AttrGroup/Attr 選單。
-- 頁面只有一個 MENU（規格屬性），底下用 BUTTON 掛 5 個權限碼：BaseAttr/SaleAttr 各自的隱藏讀權限
-- （檢視粒度跟 BaseAttrGroup 分開），以及 BaseAttrGroup/BaseAttr/SaleAttr 各自的新增(write)權限。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (14, 9, 2, '規格屬性', '/products/attr', 'products/attr/index', 'ProductAttr', 'Notebook', 'product:baseattrgroup:read', 3, 1),
    (15, 14, 3, '檢視規格屬性', NULL, NULL, NULL, NULL, 'product:baseattr:read', 1, 1),
    (16, 14, 3, '檢視銷售屬性', NULL, NULL, NULL, NULL, 'product:saleattr:read', 2, 1),
    (17, 14, 3, '新增屬性分組', NULL, NULL, NULL, NULL, 'product:baseattrgroup:write', 3, 1),
    (18, 14, 3, '新增規格屬性', NULL, NULL, NULL, NULL, 'product:baseattr:write', 4, 1),
    (19, 14, 3, '新增銷售屬性', NULL, NULL, NULL, NULL, 'product:saleattr:write', 5, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19);
