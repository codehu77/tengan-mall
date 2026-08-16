-- Phase 8：點數/會員等級管理選單，catalog 節點記得填 component（見 admin_web_crud_pattern 教訓）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (47, 0, 1, '點數管理', '/wallet', 'layout/components/ParentView', NULL, 'Wallet', NULL, 8, 1),
    (48, 47, 2, '點數規則', '/wallet/index', 'wallet/index', 'WalletRules', 'Setting', 'wallet:rules:read', 1, 1),
    (49, 48, 3, '修改規則', NULL, NULL, NULL, NULL, 'wallet:rules:write', 1, 1),
    (50, 48, 3, '調整會員點數', NULL, NULL, NULL, NULL, 'wallet:adjust:write', 2, 1),
    (51, 48, 3, '調整會員等級', NULL, NULL, NULL, NULL, 'wallet:tier:write', 3, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 47), (1, 48), (1, 49), (1, 50), (1, 51);
