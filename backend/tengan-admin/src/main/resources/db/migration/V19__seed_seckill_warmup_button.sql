-- 秒殺活動頁新增「立即預熱」按鈕權限（menu_type=3 按鈕節點，不影響選單樹），比照 V16 立即查帳按鈕同樣的模式：
-- 不用等 WarmUpScheduler 固定的每日四個時間點，demo/測試新建的場次可以立刻從 PUBLISHED 轉 ACTIVE。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (57, 53, 3, '立即預熱', NULL, NULL, NULL, NULL, 'seckill:activity:warmup', 2, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 57);
