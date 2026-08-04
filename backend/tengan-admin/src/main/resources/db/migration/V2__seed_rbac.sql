-- 開發環境種子資料：預設帳密僅供本機開發使用，正式環境上線前必須更換 admin_user 密碼。
-- password 明碼 = Admin@123（bcrypt，$2a$10 cost）

INSERT INTO role (id, role_code, role_name, status)
VALUES (1, 'SUPER_ADMIN', '超級管理員', 1);

INSERT INTO admin_user (id, username, password_hash, real_name, status)
VALUES (1, 'admin', '$2a$10$86wMqsowO.kZ3OzsF65Ls.RUFlVqN/cJRq6Aizq1voY3YXvdJ1r5m', '系統管理員', 1);

INSERT INTO user_role (admin_user_id, role_id)
VALUES (1, 1);

-- 系統管理選單樹：目錄 -> 4 個管理頁面（MENU） -> 各自的新增權限（BUTTON，不當路由節點）
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (1, 0, 1, '系統管理', '/system', NULL, NULL, 'Setting', NULL, 1, 1),
    (2, 1, 2, '管理員', '/system/user', 'system/user/index', 'SystemUser', 'User', 'system:user:read', 1, 1),
    (3, 1, 2, '角色', '/system/role', 'system/role/index', 'SystemRole', 'UserFilled', 'system:role:read', 2, 1),
    (4, 1, 2, '選單', '/system/menu', 'system/menu/index', 'SystemMenu', 'Menu', 'system:menu:read', 3, 1),
    (5, 1, 2, '操作日誌', '/system/log', 'system/log/index', 'SystemLog', 'Document', 'system:log:read', 4, 1),
    (6, 2, 3, '新增管理員', NULL, NULL, NULL, NULL, 'system:user:write', 1, 1),
    (7, 3, 3, '新增角色', NULL, NULL, NULL, NULL, 'system:role:write', 1, 1),
    (8, 4, 3, '新增選單', NULL, NULL, NULL, NULL, 'system:menu:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8);
