-- isDefault 跟 sku.mainImage 是同一件事存兩個地方（詳情頁預設圖 vs 列表縮圖），拿掉這個欄位，
-- 統一只用 sku.mainImage 當唯一的封面圖依據，images 純粹是詳情頁的其他圖。
ALTER TABLE sku_image DROP COLUMN is_default;
