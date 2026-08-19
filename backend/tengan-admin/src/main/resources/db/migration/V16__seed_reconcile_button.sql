-- Phase 8.6 排程式查帳收尾：付款記錄頁新增「立即查帳」按鈕權限（menu_type=3 按鈕節點，不影響選單樹）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (52, 45, 3, '立即查帳', NULL, NULL, NULL, NULL, 'payment:reconcile:write', 2, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 52);
