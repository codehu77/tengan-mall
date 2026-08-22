-- 訂閱管理選單：跟「付款記錄」同一層放在「支付管理」（id=44）底下，補齊後台看不到訂閱紀錄的缺口。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (58, 44, 2, '訂閱管理', '/payments/subscriptions', 'payments/subscriptions/index', 'SubscriptionList', 'Refresh', 'subscription:list:read', 2, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 58);
