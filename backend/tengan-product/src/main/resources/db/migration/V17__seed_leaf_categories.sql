-- 補上第三層（葉節點）分類，讓 BaseAttrGroup/BaseAttr/SaleAttr 有真正的種子測試對象，
-- 不用每次都手動臨時建一個分類再刪掉。
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (9,  4, '智慧型手機', NULL, 1, 3, 1),
    (10, 4, '平板電腦',   NULL, 2, 3, 1),
    (11, 5, '輕薄筆電',   NULL, 1, 3, 1),
    (12, 5, '電競筆電',   NULL, 2, 3, 1),
    (13, 6, '上衣',       NULL, 1, 3, 1),
    (14, 6, '褲裝',       NULL, 2, 3, 1),
    (15, 7, '上衣',       NULL, 1, 3, 1),
    (16, 7, '褲裝',       NULL, 2, 3, 1),
    (17, 8, '鍋具餐具',   NULL, 1, 3, 1),
    (18, 8, '小家電',     NULL, 2, 3, 1);
