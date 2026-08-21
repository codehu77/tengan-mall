-- Phase 9：行銷管理（parent_id=33，V10 建立）底下新增「秒殺活動」子選單。
-- 目錄型節點的坑（component 不可留空）這裡不適用——53 是葉節點(menu_type=2)，53/54 都已正確填值。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (53, 33, 2, '秒殺活動', '/marketing/seckill-activity', 'marketing/seckill-activity/index', 'MarketingSeckillActivity', 'AlarmClock', 'seckill:activity:read', 2, 1),
    (54, 53, 3, '編輯活動', NULL, NULL, NULL, NULL, 'seckill:activity:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 53), (1, 54);
