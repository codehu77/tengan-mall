-- Phase 6：訂單管理選單，catalog 節點記得填 component（見 admin_web_crud_pattern 教訓）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (40, 0, 1, '訂單管理', '/orders', 'layout/components/ParentView', NULL, 'Tickets', NULL, 6, 1),
    (41, 40, 2, '訂單列表', '/orders/list', 'orders/index', 'OrderList', 'List', 'order:list:read', 1, 1),
    (42, 41, 3, '出貨', NULL, NULL, NULL, NULL, 'order:ship:write', 1, 1),
    (43, 41, 3, '取消訂單', NULL, NULL, NULL, NULL, 'order:cancel:write', 2, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 40), (1, 41), (1, 42), (1, 43);
