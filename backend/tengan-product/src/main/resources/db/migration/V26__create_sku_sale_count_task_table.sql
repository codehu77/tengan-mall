CREATE TABLE sku_sale_count_task (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_sn   VARCHAR(64) NOT NULL,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_sn (order_sn)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci
  COMMENT = 'order.completed 消費冪等去重：INSERT IGNORE 搶到列（affected rows=1）才代表這個 orderSn 第一次處理';
