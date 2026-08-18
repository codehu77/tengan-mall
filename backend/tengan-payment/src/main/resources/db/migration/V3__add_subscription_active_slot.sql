-- 「同一會員同時間只能有一筆現在算數的訂閱」這條規則，原本只有 SubscribeService 在應用層查一次
-- （findCurrentByMemberId 沒查到才准建立）就直接寫入，中間沒有鎖，兩個併發的訂閱請求可以同時通過
-- 檢查、各自 INSERT 一筆，資料庫完全沒有東西會擋下來。
--
-- active_slot 補上這一層資料庫層的保護：這個訂閱「現在算數」（benefit_expired_at IS NULL）時，
-- active_slot 就是它的 member_id；一旦 benefit_expired_at 被排程標記（見 markBenefitExpired），
-- active_slot 一起清成 NULL。對這個欄位建 UNIQUE KEY——MySQL 的 UNIQUE KEY 允許多個 NULL 共存，
-- 只有兩筆 active_slot 同時是同一個非 NULL 值時才會撞鍵，等於只限制「同一會員同時間最多一筆現在
-- 算數的訂閱」，不影響同一會員歷史上訂閱多次留下的多筆記錄。
ALTER TABLE subscription ADD COLUMN active_slot BIGINT UNSIGNED NULL COMMENT '現在算數時=member_id，已結束時=NULL，供 uk_active_slot 擋併發重複訂閱' AFTER member_id;

UPDATE subscription SET active_slot = member_id WHERE benefit_expired_at IS NULL;

ALTER TABLE subscription ADD UNIQUE KEY uk_active_slot (active_slot);
