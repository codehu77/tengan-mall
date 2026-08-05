-- 搜尋管理：掛在既有「商品管理」目錄(id=9)底下，單一頁面只有一顆「重建索引」按鈕，
-- 沒有 CRUD，permission_code 直接用 search:reindex，不用另外拆 read/write。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (23, 9, 2, '搜尋管理', '/products/search', 'products/search/index', 'ProductSearch', 'Search', 'search:reindex', 5, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 23);
