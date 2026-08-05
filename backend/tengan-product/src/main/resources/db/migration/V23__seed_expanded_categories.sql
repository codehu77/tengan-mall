-- 擴充分類樹到 10 個一級、每級約 3 個二級、每個二級約 5 個三級，供 Demo 展示分類/品牌/規格頁面用。
-- 既有分類（id 1-18）完全不動，這裡只新增節點。id 從 19 開始接續。

-- === 3C 產品（id=1）新增二級 ===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (19, 1, '電腦與周邊', 'ep:monitor', 3, 2, 1),
    (20, 1, '電玩與遊戲', 'ep:cpu', 4, 2, 1);

-- 手機平板（id=4）補三級
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (21, 4, '穿戴裝置', NULL, 3, 3, 1),
    (22, 4, '手機殼與保護貼', NULL, 4, 3, 1),
    (23, 4, '行動電源與充電器', NULL, 5, 3, 1);

-- 筆記型電腦（id=5）補三級
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (24, 5, '商用筆電', NULL, 3, 3, 1),
    (25, 5, '二合一筆電', NULL, 4, 3, 1),
    (26, 5, '筆電包與周邊', NULL, 5, 3, 1);

-- 電腦與周邊（id=19）
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (27, 19, '桌上型電腦', NULL, 1, 3, 1),
    (28, 19, '螢幕顯示器', NULL, 2, 3, 1),
    (29, 19, '鍵盤滑鼠', NULL, 3, 3, 1),
    (30, 19, '儲存裝置', NULL, 4, 3, 1),
    (31, 19, '網通設備', NULL, 5, 3, 1);

-- 電玩與遊戲（id=20）—— Switch/PlayStation/Xbox 等主機、遊戲片、周邊都放這層
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (32, 20, '家用主機', NULL, 1, 3, 1),
    (33, 20, '掌上型主機', NULL, 2, 3, 1),
    (34, 20, '遊戲片', NULL, 3, 3, 1),
    (35, 20, '手把與周邊', NULL, 4, 3, 1),
    (36, 20, '遊戲點數與會員', NULL, 5, 3, 1);

-- === 服飾配件（id=2）新增二級 ===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (37, 2, '配件飾品', 'ep:collection', 3, 2, 1);

-- 男裝（id=6）補三級
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (38, 6, '外套', NULL, 3, 3, 1),
    (39, 6, '內著', NULL, 4, 3, 1),
    (40, 6, '男鞋', NULL, 5, 3, 1);

-- 女裝（id=7）補三級
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (41, 7, '洋裝', NULL, 3, 3, 1),
    (42, 7, '外套', NULL, 4, 3, 1),
    (43, 7, '女鞋', NULL, 5, 3, 1);

-- 配件飾品（id=37）
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (44, 37, '包包', NULL, 1, 3, 1),
    (45, 37, '手錶', NULL, 2, 3, 1),
    (46, 37, '珠寶飾品', NULL, 3, 3, 1),
    (47, 37, '皮帶帽飾', NULL, 4, 3, 1),
    (48, 37, '太陽眼鏡', NULL, 5, 3, 1);

-- === 居家生活（id=3）新增二級 ===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (49, 3, '傢俱寢具', 'ep:house', 2, 2, 1),
    (50, 3, '清潔用品', 'ep:brush-filled', 3, 2, 1);

-- 廚房用品（id=8）補三級
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (51, 8, '廚房收納', NULL, 3, 3, 1),
    (52, 8, '烘焙用具', NULL, 4, 3, 1),
    (53, 8, '保鮮容器', NULL, 5, 3, 1);

-- 傢俱寢具（id=49）
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (54, 49, '沙發', NULL, 1, 3, 1),
    (55, 49, '床架床墊', NULL, 2, 3, 1),
    (56, 49, '收納櫃', NULL, 3, 3, 1),
    (57, 49, '桌椅', NULL, 4, 3, 1),
    (58, 49, '寢具組', NULL, 5, 3, 1);

-- 清潔用品（id=50）
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (59, 50, '洗衣清潔', NULL, 1, 3, 1),
    (60, 50, '居家清潔', NULL, 2, 3, 1),
    (61, 50, '衛浴用品', NULL, 3, 3, 1),
    (62, 50, '除濕防潮', NULL, 4, 3, 1),
    (63, 50, '芳香除臭', NULL, 5, 3, 1);

-- === 美妝保養（新一級）===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (64, 0, '美妝保養', 'ep:magic-stick', 4, 1, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (65, 64, '臉部保養', NULL, 1, 2, 1),
    (66, 64, '彩妝', NULL, 2, 2, 1),
    (67, 64, '香氛美髮', NULL, 3, 2, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (68, 65, '化妝水', NULL, 1, 3, 1),
    (69, 65, '精華液', NULL, 2, 3, 1),
    (70, 65, '乳液乳霜', NULL, 3, 3, 1),
    (71, 65, '面膜', NULL, 4, 3, 1),
    (72, 65, '卸妝清潔', NULL, 5, 3, 1),
    (73, 66, '底妝', NULL, 1, 3, 1),
    (74, 66, '眼妝', NULL, 2, 3, 1),
    (75, 66, '唇妝', NULL, 3, 3, 1),
    (76, 66, '腮紅修容', NULL, 4, 3, 1),
    (77, 66, '美甲', NULL, 5, 3, 1),
    (78, 67, '香水', NULL, 1, 3, 1),
    (79, 67, '洗髮護髮', NULL, 2, 3, 1),
    (80, 67, '造型用品', NULL, 3, 3, 1),
    (81, 67, '身體乳', NULL, 4, 3, 1),
    (82, 67, '男士保養', NULL, 5, 3, 1);

-- === 運動戶外（新一級）===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (83, 0, '運動戶外', 'ep:basketball', 5, 1, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (84, 83, '運動服飾', NULL, 1, 2, 1),
    (85, 83, '健身器材', NULL, 2, 2, 1),
    (86, 83, '登山露營', NULL, 3, 2, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (87, 84, '運動上衣', NULL, 1, 3, 1),
    (88, 84, '運動褲', NULL, 2, 3, 1),
    (89, 84, '運動內衣', NULL, 3, 3, 1),
    (90, 84, '機能外套', NULL, 4, 3, 1),
    (91, 84, '運動鞋', NULL, 5, 3, 1),
    (92, 85, '重量訓練', NULL, 1, 3, 1),
    (93, 85, '有氧器材', NULL, 2, 3, 1),
    (94, 85, '瑜珈用品', NULL, 3, 3, 1),
    (95, 85, '運動配件', NULL, 4, 3, 1),
    (96, 85, '運動包', NULL, 5, 3, 1),
    (97, 86, '帳篷睡袋', NULL, 1, 3, 1),
    (98, 86, '登山背包', NULL, 2, 3, 1),
    (99, 86, '露營炊具', NULL, 3, 3, 1),
    (100, 86, '機能服飾', NULL, 4, 3, 1),
    (101, 86, '戶外配件', NULL, 5, 3, 1);

-- === 母嬰用品（新一級）===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (102, 0, '母嬰用品', 'ep:present', 6, 1, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (103, 102, '嬰幼兒用品', NULL, 1, 2, 1),
    (104, 102, '孕婦用品', NULL, 2, 2, 1),
    (105, 102, '兒童用品', NULL, 3, 2, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (106, 103, '尿布濕巾', NULL, 1, 3, 1),
    (107, 103, '奶粉副食品', NULL, 2, 3, 1),
    (108, 103, '奶瓶用品', NULL, 3, 3, 1),
    (109, 103, '嬰兒澡盆', NULL, 4, 3, 1),
    (110, 103, '嬰兒安全防護', NULL, 5, 3, 1),
    (111, 104, '孕婦裝', NULL, 1, 3, 1),
    (112, 104, '產後用品', NULL, 2, 3, 1),
    (113, 104, '產檢用品', NULL, 3, 3, 1),
    (114, 104, '哺乳用品', NULL, 4, 3, 1),
    (115, 104, '孕婦保健食品', NULL, 5, 3, 1),
    (116, 105, '嬰兒玩具', NULL, 1, 3, 1),
    (117, 105, '嬰兒推車', NULL, 2, 3, 1),
    (118, 105, '汽座安全座椅', NULL, 3, 3, 1),
    (119, 105, '學習用品', NULL, 4, 3, 1),
    (120, 105, '兒童餐具', NULL, 5, 3, 1);

-- === 食品飲料（新一級）===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (121, 0, '食品飲料', 'ep:dish', 7, 1, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (122, 121, '零食甜點', NULL, 1, 2, 1),
    (123, 121, '飲品沖泡', NULL, 2, 2, 1),
    (124, 121, '生鮮保健', NULL, 3, 2, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (125, 122, '餅乾零食', NULL, 1, 3, 1),
    (126, 122, '巧克力糖果', NULL, 2, 3, 1),
    (127, 122, '堅果乾果', NULL, 3, 3, 1),
    (128, 122, '蜜餞果乾', NULL, 4, 3, 1),
    (129, 122, '甜點烘焙', NULL, 5, 3, 1),
    (130, 123, '咖啡', NULL, 1, 3, 1),
    (131, 123, '茶葉茶包', NULL, 2, 3, 1),
    (132, 123, '沖泡飲品', NULL, 3, 3, 1),
    (133, 123, '果汁飲料', NULL, 4, 3, 1),
    (134, 123, '酒類', NULL, 5, 3, 1),
    (135, 124, '生鮮食材', NULL, 1, 3, 1),
    (136, 124, '冷凍食品', NULL, 2, 3, 1),
    (137, 124, '保健食品', NULL, 3, 3, 1),
    (138, 124, '進口食品', NULL, 4, 3, 1),
    (139, 124, '有機食品', NULL, 5, 3, 1);

-- === 圖書文具（新一級）===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (140, 0, '圖書文具', 'ep:notebook', 8, 1, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (141, 140, '書籍', NULL, 1, 2, 1),
    (142, 140, '文具用品', NULL, 2, 2, 1),
    (143, 140, '玩具嗜好', NULL, 3, 2, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (144, 141, '文學小說', NULL, 1, 3, 1),
    (145, 141, '商業理財', NULL, 2, 3, 1),
    (146, 141, '兒童繪本', NULL, 3, 3, 1),
    (147, 141, '語言學習', NULL, 4, 3, 1),
    (148, 141, '漫畫輕小說', NULL, 5, 3, 1),
    (149, 142, '辦公文具', NULL, 1, 3, 1),
    (150, 142, '筆記手帳', NULL, 2, 3, 1),
    (151, 142, '繪畫美術', NULL, 3, 3, 1),
    (152, 142, '書包收納', NULL, 4, 3, 1),
    (153, 142, '印章印材', NULL, 5, 3, 1),
    (154, 143, '益智玩具', NULL, 1, 3, 1),
    (155, 143, '桌遊', NULL, 2, 3, 1),
    (156, 143, '樂器', NULL, 3, 3, 1),
    (157, 143, '模型公仔', NULL, 4, 3, 1),
    (158, 143, 'DIY 手作', NULL, 5, 3, 1);

-- === 汽機車用品（新一級）===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (159, 0, '汽機車用品', 'ep:van', 9, 1, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (160, 159, '汽車百貨', NULL, 1, 2, 1),
    (161, 159, '機車百貨', NULL, 2, 2, 1),
    (162, 159, '交通配件', NULL, 3, 2, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (163, 160, '車用電子', NULL, 1, 3, 1),
    (164, 160, '車內用品', NULL, 2, 3, 1),
    (165, 160, '車用清潔', NULL, 3, 3, 1),
    (166, 160, '輪胎保養', NULL, 4, 3, 1),
    (167, 160, '行車記錄器', NULL, 5, 3, 1),
    (168, 161, '安全帽', NULL, 1, 3, 1),
    (169, 161, '機車零件', NULL, 2, 3, 1),
    (170, 161, '機車周邊', NULL, 3, 3, 1),
    (171, 161, '雨衣雨鞋', NULL, 4, 3, 1),
    (172, 161, '機車清潔', NULL, 5, 3, 1),
    (173, 162, '車用充電器', NULL, 1, 3, 1),
    (174, 162, 'GPS 導航', NULL, 2, 3, 1),
    (175, 162, '兒童安全座椅', NULL, 3, 3, 1),
    (176, 162, '洗車用品', NULL, 4, 3, 1),
    (177, 162, '露營車用品', NULL, 5, 3, 1);

-- === 寵物用品（新一級）===
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (178, 0, '寵物用品', 'ep:collection-tag', 10, 1, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (179, 178, '貓咪用品', NULL, 1, 2, 1),
    (180, 178, '狗狗用品', NULL, 2, 2, 1),
    (181, 178, '水族兩棲', NULL, 3, 2, 1);
INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (182, 179, '貓飼料', NULL, 1, 3, 1),
    (183, 179, '貓砂與貓砂盆', NULL, 2, 3, 1),
    (184, 179, '貓抓板', NULL, 3, 3, 1),
    (185, 179, '貓玩具', NULL, 4, 3, 1),
    (186, 179, '貓咪保健', NULL, 5, 3, 1),
    (187, 180, '狗飼料', NULL, 1, 3, 1),
    (188, 180, '牽繩項圈', NULL, 2, 3, 1),
    (189, 180, '狗窩狗籠', NULL, 3, 3, 1),
    (190, 180, '狗玩具', NULL, 4, 3, 1),
    (191, 180, '狗狗保健', NULL, 5, 3, 1),
    (192, 181, '魚缸水族箱', NULL, 1, 3, 1),
    (193, 181, '飼料與水質', NULL, 2, 3, 1),
    (194, 181, '水族造景', NULL, 3, 3, 1),
    (195, 181, '爬蟲用品', NULL, 4, 3, 1),
    (196, 181, '寵物清潔用品', NULL, 5, 3, 1);
