-- Phase 10：行銷管理（parent_id=33，V10 建立）底下新增「輪播圖管理」子選單。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (59, 33, 2, '輪播圖管理', '/marketing/banner', 'marketing/banner/index', 'MarketingBanner', 'Picture', 'media:banner:read', 4, 1),
    (60, 59, 3, '編輯輪播圖', NULL, NULL, NULL, NULL, 'media:banner:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 59), (1, 60);
