CREATE TABLE member_category_interest (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT NOT NULL,
    category_id   BIGINT NOT NULL,
    source        TINYINT NOT NULL COMMENT '1=VIEW(瀏覽商品詳情頁) 2=SEARCH(搜尋/分類瀏覽推回)',
    interest_date DATE NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_member_category_source_date (member_id, category_id, source, interest_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci
  COMMENT = '猜你喜歡的分類興趣訊號。UNIQUE KEY 讓同一天同分類同來源只會有一列，這是刻意設計：
  直接把「算興趣分數」簡化成「算列數」，一次爆量瀏覽/搜尋只會貢獻 1 天份，不會被單次行為洗分數';
