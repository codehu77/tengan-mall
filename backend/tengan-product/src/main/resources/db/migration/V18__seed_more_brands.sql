-- 擴充品牌清單，涵蓋新增葉節點分類會用到的領域（3C/服飾/廚具家電）。
INSERT INTO brand (id, name, logo, descript, first_letter, sort, status) VALUES
    (4,  'Samsung', 'https://placehold.co/100x40?text=Samsung', '三星電子，韓國消費性電子大廠', 'S', 4,  1),
    (5,  'Xiaomi',  'https://placehold.co/100x40?text=Xiaomi',  '小米，性價比科技品牌',         'X', 5,  1),
    (6,  'ASUS',    'https://placehold.co/100x40?text=ASUS',    '華碩，台灣筆電與主機板大廠',   'A', 6,  1),
    (7,  'Acer',    'https://placehold.co/100x40?text=Acer',    '宏碁，台灣個人電腦品牌',       'A', 7,  1),
    (8,  'MSI',     'https://placehold.co/100x40?text=MSI',     '微星，電競筆電與顯示卡品牌',   'M', 8,  1),
    (9,  'Nike',    'https://placehold.co/100x40?text=Nike',    '耐吉，美國運動服飾品牌',       'N', 9,  1),
    (10, 'Adidas',  'https://placehold.co/100x40?text=Adidas',  '愛迪達，德國運動服飾品牌',     'A', 10, 1),
    (11, 'Zara',    'https://placehold.co/100x40?text=Zara',    'Zara，西班牙快時尚品牌',       'Z', 11, 1),
    (12, 'Tefal',   'https://placehold.co/100x40?text=Tefal',   '特福，法國廚具品牌',           'T', 12, 1),
    (13, 'Philips', 'https://placehold.co/100x40?text=Philips', '飛利浦，荷蘭家電品牌',         'P', 13, 1);
