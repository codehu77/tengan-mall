-- 即將開賣 Phase B：庫存管理底下新增「流量閘門」頁面，讓管理員看得到預熱/結算狀態，也能手動立即
-- 預熱（不用乾等 GateWarmUpScheduler 固定的每日四個時間點），完全比照 V19 秒殺「立即預熱」按鈕同樣模式。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (61, 27, 2, '流量閘門', '/inventory/gate', 'inventory/gate/index', 'InventoryGate', 'Lightning', 'inventory:gate:read', 4, 1),
    (62, 61, 3, '立即預熱', NULL, NULL, NULL, NULL, 'inventory:gate:warmup', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 61), (1, 62);
