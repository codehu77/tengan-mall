-- 商品管理選單樹：目錄 -> 2 個管理頁面（MENU：分類/品牌） -> 各自的新增權限（BUTTON）
-- 這次範圍只做 Category + Brand（見 backend_dev_plan.md 2026-08-02 條目），AttrGroup/Attr/Spu-Sku
-- 之後照同樣模式擴充，不用等這批全部做完才建選單。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (9, 0, 1, '商品管理', '/products', NULL, NULL, 'Goods', NULL, 2, 1),
    (10, 9, 2, '商品分類', '/products/category', 'products/category/index', 'ProductCategory', 'Menu', 'product:category:read', 1, 1),
    (11, 9, 2, '品牌管理', '/products/brand', 'products/brand/index', 'ProductBrand', 'PriceTag', 'product:brand:read', 2, 1),
    (12, 10, 3, '新增分類', NULL, NULL, NULL, NULL, 'product:category:write', 1, 1),
    (13, 11, 3, '新增品牌', NULL, NULL, NULL, NULL, 'product:brand:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 9), (1, 10), (1, 11), (1, 12), (1, 13);
