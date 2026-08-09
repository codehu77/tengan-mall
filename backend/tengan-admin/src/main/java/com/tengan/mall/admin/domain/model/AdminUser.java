package com.tengan.mall.admin.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 聚合根。{@code id} 在 {@link #create} 產生時為 null，由 Repository 寫入後回填
 * （見 ddd-standards.md「Aggregate Root」）。{@code roleIds} 是這個管理員目前擁有的角色集合，
 * 持久層對應 user_role join table。
 */
public class AdminUser {

    private AdminUserId id;
    private final AdminUsername username;
    private String passwordHash;
    private String realName;
    private String avatarUrl;
    private AdminUserStatus status;
    private final Set<RoleId> roleIds;

    private AdminUser(AdminUserId id, AdminUsername username, String passwordHash, String realName,
            String avatarUrl, AdminUserStatus status, Set<RoleId> roleIds) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.realName = realName;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.roleIds = new HashSet<>(roleIds);
    }

    public static AdminUser create(AdminUsername username, String passwordHash, String realName) {
        return new AdminUser(null, username, passwordHash, realName, null, AdminUserStatus.ACTIVE, Set.of());
    }

    public static AdminUser reconstitute(AdminUserId id, AdminUsername username, String passwordHash,
            String realName, String avatarUrl, AdminUserStatus status, Set<RoleId> roleIds) {
        return new AdminUser(id, username, passwordHash, realName, avatarUrl, status, roleIds);
    }

    public void assignId(AdminUserId id) {
        if (this.id != null) {
            throw new IllegalStateException("AdminUser 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void activate() {
        this.status = AdminUserStatus.ACTIVE;
    }

    public void disable() {
        this.status = AdminUserStatus.DISABLED;
    }

    /** 角色指派在 UI 上是「這個管理員現在該有哪些角色」的整組替換，不是逐一 add/remove。 */
    public void replaceRoles(Set<RoleId> newRoleIds) {
        this.roleIds.clear();
        this.roleIds.addAll(newRoleIds);
    }

    /** 管理員自己編輯個人資訊：改名字、換頭像。 */
    public void updateProfile(String realName, String avatarUrl) {
        this.realName = realName;
        this.avatarUrl = avatarUrl;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public boolean isActive() {
        return status == AdminUserStatus.ACTIVE;
    }

    public AdminUserId getId() {
        return id;
    }

    public AdminUsername getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRealName() {
        return realName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public AdminUserStatus getStatus() {
        return status;
    }

    public Set<RoleId> getRoleIds() {
        return Collections.unmodifiableSet(roleIds);
    }
}
