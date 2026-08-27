-- 防超賣保護入口搬回 SPU 列表頁，SPU 廣播套用到底下所有 SKU：搬移＋改名既有的 id=63 按鈕節點
-- （V24 建的「設定庫存流量閘門」，掛在庫存列表 id=28 底下），parent_id=20 是 SPU 列表選單
-- （見 V7__seed_spu_menu.sql）。權限碼 inventory:gate:write 跟既有 role_menu 授權都不變，
-- 只是搬到正確的父選單底下、換掉顯示文字。
UPDATE menu SET parent_id = 20, title = '設定防超賣保護', sort_order = 2 WHERE id = 63;
