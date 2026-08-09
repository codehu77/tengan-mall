-- Phase 5：庫存管理 + 行銷管理(優惠券) 選單樹。catalog 節點記得填 component（見 admin_web_crud_pattern 教訓，
-- 之前 V3/V5 各修過一次漏填的坑，這次直接照 V9 會員管理已經正確的寫法，不再重複踩）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (27, 0, 1, '庫存管理', '/inventory', 'layout/components/ParentView', NULL, 'Box', NULL, 4, 1),
    (28, 27, 2, '庫存列表', '/inventory/stock', 'inventory/stock/index', 'InventoryStock', 'List', 'inventory:stock:read', 1, 1),
    (29, 28, 3, '調整庫存', NULL, NULL, NULL, NULL, 'inventory:stock:write', 1, 1),
    (30, 27, 2, '倉庫管理', '/inventory/warehouse', 'inventory/warehouse/index', 'InventoryWarehouse', 'OfficeBuilding', 'inventory:warehouse:read', 2, 1),
    (31, 30, 3, '新增倉庫', NULL, NULL, NULL, NULL, 'inventory:warehouse:write', 1, 1),
    (32, 27, 2, '工作單查詢', '/inventory/task', 'inventory/task/index', 'InventoryTask', 'Document', 'inventory:task:read', 3, 1),
    (33, 0, 1, '行銷管理', '/marketing', 'layout/components/ParentView', NULL, 'Present', NULL, 5, 1),
    (34, 33, 2, '優惠券模板', '/marketing/coupon-template', 'marketing/coupon-template/index', 'MarketingCouponTemplate', 'Ticket', 'coupon:template:read', 1, 1),
    (35, 34, 3, '編輯模板', NULL, NULL, NULL, NULL, 'coupon:template:write', 1, 1),
    (36, 34, 3, '核發優惠券', NULL, NULL, NULL, NULL, 'coupon:grant:write', 2, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 27), (1, 28), (1, 29), (1, 30), (1, 31), (1, 32), (1, 33), (1, 34), (1, 35), (1, 36);
