-- 每個 bounded context 各自一個獨立 schema，不共用（對應 ddd-standards.md 第九節）
-- tengan-search（純 ES）、tengan-gateway（無狀態）不需要自己的資料庫
-- tengan-cart：會員購物車走 MySQL（長期保存，不設 TTL），訪客購物車仍走 Redis（短 TTL，即用即棄）

CREATE DATABASE IF NOT EXISTS nacos_config DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_member DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_cart DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_inventory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_coupon DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_wallet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_seckill DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_media DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS tengan_mall_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- xxl_job 資料庫需另外匯入官方 xxl-job 專案內的 tables_xxl_job.sql（僅在啟用 --profile full 時需要）
CREATE DATABASE IF NOT EXISTS xxl_job DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
