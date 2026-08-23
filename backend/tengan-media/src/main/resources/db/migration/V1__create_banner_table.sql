CREATE TABLE banner (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    image_url   VARCHAR(500) NOT NULL,
    link_url    VARCHAR(500) NULL COMMENT '點擊輪播圖後導向的網址，可留空',
    title       VARCHAR(100) NULL COMMENT '純後台識別用，前台不一定顯示',
    sort_order  INT          NOT NULL DEFAULT 0,
    enabled     TINYINT      NOT NULL DEFAULT 1 COMMENT '停用後不出現在首頁公開端點',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
