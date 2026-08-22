-- 場次機制：行銷管理（parent_id=33）底下新增「秒殺場次」子選單，跟「秒殺活動」(id=53) 同一層級。
-- 葉節點(menu_type=2)，沿用既有 seckill:activity:* 權限碼（同一個功能領域，不另開新權限）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (55, 33, 2, '秒殺場次', '/marketing/seckill-session', 'marketing/seckill-session/index', 'MarketingSeckillSession', 'Clock', 'seckill:activity:read', 3, 1),
    (56, 55, 3, '編輯場次', NULL, NULL, NULL, NULL, 'seckill:activity:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 55), (1, 56);
