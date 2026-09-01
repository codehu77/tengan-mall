-- Phase 14：tengan-auth 拿掉 username，這裡跟著同步（member.username 原本只是 account.username 的快照）。
ALTER TABLE member
    DROP COLUMN username,
    ADD COLUMN email VARCHAR(100) NULL AFTER phone;
