-- Phase 14：登入註冊流程現代化重構，拿掉 username，身份改錨定在已驗證的 phone/email。
ALTER TABLE account
    DROP INDEX uk_account_username,
    DROP COLUMN username;
