-- 個人資訊編輯功能：頭像網址（本機磁碟儲存，見 infrastructure/storage/LocalFileStorageAdapter）。
ALTER TABLE admin_user ADD COLUMN avatar_url VARCHAR(255) NULL AFTER real_name;
