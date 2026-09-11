-- 運費機制：在既有「訂單管理」父選單（id=40，見 V13__seed_order_menu.sql）底下新增「運費設定」子頁面，
-- catalog 節點記得填 component（見 admin_web_crud_pattern 教訓）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (70, 40, 2, '運費設定', '/orders/freight', 'orders/freight', 'OrderFreight', 'Van', 'order:freight:read', 2, 1),
    (71, 70, 3, '修改運費設定', NULL, NULL, NULL, NULL, 'order:freight:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 70), (1, 71);
