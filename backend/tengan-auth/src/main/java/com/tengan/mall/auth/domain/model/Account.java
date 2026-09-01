package com.tengan.mall.auth.domain.model;

/**
 * 聚合根。{@code id} 在 {@link #create} 產生時為 null，由 Repository 寫入後回填
 * （見 ddd-standards.md「Aggregate Root」：create 套用預設規則，reconstitute 純還原不觸發規則）。
 *
 * <p>身份錨定在已驗證的 phone/email，不再有 username。invariant「phone 或 email 至少一個有值，
 * 或至少有一筆 account_oauth_binding」中，OAuth 綁定那一腳屬於 Phase 13（尚未實作），這裡只驗證
 * phone/email 二選一——Phase 13 開工時要在建立 OAuth-only 帳號的路徑補上第三種例外。
 */
public class Account {

    private AccountId id;
    private final Phone phone;
    private final Email email;
    private String passwordHash;
    private AccountStatus status;

    private Account(AccountId id, Phone phone, Email email, String passwordHash, AccountStatus status) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    /** 本地密碼註冊用，phone/email 至少一個有值。 */
    public static Account create(Phone phone, Email email, String passwordHash) {
        if (phone == null && email == null) {
            throw new IllegalArgumentException("Account 必須至少有 phone 或 email 其中一個");
        }
        return new Account(null, phone, email, passwordHash, AccountStatus.ACTIVE);
    }

    public static Account reconstitute(AccountId id, Phone phone, Email email, String passwordHash,
            AccountStatus status) {
        return new Account(id, phone, email, passwordHash, status);
    }

    public void assignId(AccountId id) {
        if (this.id != null) {
            throw new IllegalStateException("Account 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public void disable() {
        this.status = AccountStatus.DISABLED;
    }

    public void enable() {
        this.status = AccountStatus.ACTIVE;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public AccountId getId() {
        return id;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
