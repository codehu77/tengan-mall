-- 會員管理選單：新的頂層目錄（不掛在商品管理底下），比照既有 read/write 兩個權限碼慣例
-- （ban/unban 共用同一個 write 權限碼，跟 category/brand 的 show/hide 共用 write 是同一個模式）。
INSERT INTO menu (id, parent_id, menu_type, title, path, component, route_name, icon, permission_code, sort_order, status)
VALUES
    (24, 0, 1, '會員管理', '/members', 'layout/components/ParentView', NULL, 'User', NULL, 3, 1),
    (25, 24, 2, '會員列表', '/members/list', 'members/index', 'MemberList', 'UserFilled', 'member:read', 1, 1),
    (26, 25, 3, '停權/復權', NULL, NULL, NULL, NULL, 'member:write', 1, 1);

INSERT INTO role_menu (role_id, menu_id)
VALUES (1, 24), (1, 25), (1, 26);
