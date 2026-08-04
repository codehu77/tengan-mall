package com.tengan.mall.admin.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 聚合根。{@code roleCode} 建立後不可變更（見 {@link RoleCode}），{@code menuIds} 是這個角色
 * 目前被授權的選單集合，持久層對應 role_menu join table。
 */
public class Role {

    private RoleId id;
    private final RoleCode roleCode;
    private String roleName;
    private RoleStatus status;
    private final Set<MenuId> menuIds;

    private Role(RoleId id, RoleCode roleCode, String roleName, RoleStatus status, Set<MenuId> menuIds) {
        this.id = id;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.status = status;
        this.menuIds = new HashSet<>(menuIds);
    }

    public static Role create(RoleCode roleCode, String roleName) {
        return new Role(null, roleCode, roleName, RoleStatus.ACTIVE, Set.of());
    }

    public static Role reconstitute(RoleId id, RoleCode roleCode, String roleName, RoleStatus status,
            Set<MenuId> menuIds) {
        return new Role(id, roleCode, roleName, status, menuIds);
    }

    public void assignId(RoleId id) {
        if (this.id != null) {
            throw new IllegalStateException("Role 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void rename(String newRoleName) {
        if (newRoleName == null || newRoleName.isBlank()) {
            throw new IllegalArgumentException("roleName 不可為空");
        }
        this.roleName = newRoleName;
    }

    public void activate() {
        this.status = RoleStatus.ACTIVE;
    }

    public void disable() {
        this.status = RoleStatus.DISABLED;
    }

    /** 選單授權在 UI 上是「這個角色現在該有哪些選單」的整組替換，不是逐一 add/remove。 */
    public void grantMenus(Set<MenuId> newMenuIds) {
        this.menuIds.clear();
        this.menuIds.addAll(newMenuIds);
    }

    public boolean isActive() {
        return status == RoleStatus.ACTIVE;
    }

    public RoleId getId() {
        return id;
    }

    public RoleCode getRoleCode() {
        return roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public RoleStatus getStatus() {
        return status;
    }

    public Set<MenuId> getMenuIds() {
        return Collections.unmodifiableSet(menuIds);
    }
}
