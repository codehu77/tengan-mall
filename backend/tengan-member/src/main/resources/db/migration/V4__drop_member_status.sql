-- member.status 拿掉：這跟 tengan-auth 的 account.status 是同一件事被存了兩份，member.status
-- 從沒被任何業務規則讀取過，純顯示用複本。真正擋登入的狀態只存在 tengan-auth；後台需要顯示/
-- 切換停權狀態時由 tengan-admin 即時去問 tengan-auth 組出來，不在這裡存第二份（見設計討論）。
ALTER TABLE member DROP COLUMN status;

-- member_oper_log 唯一的呼叫者是 ban/unban（管理端寫入動作才需要稽核），兩者都拿掉了，
-- 這張表跟著清掉，不留沒有任何程式碼在用的空表。
DROP TABLE member_oper_log;
