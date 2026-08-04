package com.tengan.mall.admin.domain.model;

/**
 * 大寫+底線的角色代碼（例如 SUPER_ADMIN），建立後不可變更——角色代碼是給程式判斷用的
 * 穩定識別碼，跟可隨時改的 {@code roleName}（顯示用）分開。
 */
public record RoleCode(String value) {

    public RoleCode {
        if (value == null || !value.matches("^[A-Z][A-Z0-9_]{1,49}$")) {
            throw new IllegalArgumentException("roleCode 必須是大寫字母開頭、只能包含大寫字母/數字/底線: " + value);
        }
    }
}
