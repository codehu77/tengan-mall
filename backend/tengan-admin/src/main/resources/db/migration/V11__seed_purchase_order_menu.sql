-- 輕量版採購單選單，掛在既有「庫存管理」目錄（id=27）底下，比照 V10 的寫法。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (37, 27, 2, '採購單', '/inventory/purchase-order', 'inventory/purchase-order/index', 'InventoryPurchaseOrder', 'ShoppingCart', 'inventory:purchase:read', 4, 1),
    (38, 37, 3, '新增採購單', NULL, NULL, NULL, NULL, 'inventory:purchase:write', 1, 1),
    (39, 37, 3, '收貨', NULL, NULL, NULL, NULL, 'inventory:purchase:write', 2, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 37), (1, 38), (1, 39);
