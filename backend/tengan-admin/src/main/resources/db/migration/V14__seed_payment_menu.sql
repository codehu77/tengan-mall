-- Phase 7：支付管理選單，catalog 節點記得填 component（見 admin_web_crud_pattern 教訓）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (44, 0, 1, '支付管理', '/payments', 'layout/components/ParentView', NULL, 'CreditCard', NULL, 7, 1),
    (45, 44, 2, '付款記錄', '/payments/list', 'payments/index', 'PaymentList', 'List', 'payment:list:read', 1, 1),
    (46, 45, 3, '啟用/停用付款方式', NULL, NULL, NULL, NULL, 'payment:method:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 44), (1, 45), (1, 46);
