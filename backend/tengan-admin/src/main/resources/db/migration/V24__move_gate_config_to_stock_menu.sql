-- 庫存流量閘門搬出 SPU 表單，改由 tengan-inventory 直接管理：操作入口改放庫存列表頁（管理員在這裡
-- 已經看得到真實庫存數字），不是 V23 的監控頁——監控頁(id=61)保留不動，只改標題跟新舊命名對齊。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (63, 28, 3, '設定庫存流量閘門', NULL, NULL, NULL, NULL, 'inventory:gate:write', 2, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 63);

UPDATE menu SET title = '庫存流量閘門' WHERE id = 61;
