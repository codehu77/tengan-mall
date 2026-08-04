package com.tengan.mall.auth.domain.model;

/**
 * 聚合根。{@code id} 在 {@link #create} 產生時為 null，由 Repository 寫入後回填
 * （見 ddd-standards.md「Aggregate Root」：create 套用預設規則，reconstitute 純還原不觸發規則）。
 */
public class Account {

    private AccountId id;
    private final Username username;
    private final Phone phone;
    private final String passwordHash;
    private AccountStatus status;

    private Account(AccountId id, Username username, Phone phone, String passwordHash, AccountStatus status) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public static Account create(Username username, Phone phone, String passwordHash) {
        return new Account(null, username, phone, passwordHash, AccountStatus.ACTIVE);
    }

    public static Account reconstitute(AccountId id, Username username, Phone phone, String passwordHash,
            AccountStatus status) {
        return new Account(id, username, phone, passwordHash, status);
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

    public AccountId getId() {
        return id;
    }

    public Username getUsername() {
        return username;
    }

    public Phone getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
