-- 點數帳本。type 是不可變起因（EARN/REDEEM/EXPIRE/ADJUST），status 只描述兩種生命週期：
-- EARN 的 reserve(PENDING)→confirm(CONFIRMED)，REDEEM 被取消訂單撤銷(CONFIRMED→REVERSED)。
-- EXPIRE/ADJUST 一律直接以 CONFIRMED 寫入、終生不轉換（見 domain/model/PointsTransaction 類別說明）。
-- 可用餘額 = SUM(points) WHERE status=CONFIRMED，REVERSED 的 REDEEM 列自然被排除，不需要額外沖正查詢。
CREATE TABLE points_transaction (
    id                     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    member_id              BIGINT UNSIGNED NOT NULL,
    type                   TINYINT         NOT NULL COMMENT '1=EARN 2=REDEEM 3=EXPIRE 4=ADJUST',
    status                 TINYINT         NOT NULL COMMENT '1=PENDING 2=CONFIRMED 3=REVERSED',
    points                 INT             NOT NULL COMMENT '有號整數，EARN/ADJUST可正可負，REDEEM/EXPIRE恆負',
    balance_after          INT             NULL COMMENT '寫入當下的餘額快照，best-effort 展示用，非權威來源',
    title                  VARCHAR(100)    NOT NULL,
    description            VARCHAR(255)    NULL,
    order_sn               VARCHAR(64)     NULL COMMENT 'EARN/REDEEM 才有值，業務自然鍵引用，不建物理外鍵',
    channel                VARCHAR(30)     NOT NULL COMMENT 'ORDER_COMPLETION/CHECKOUT/SYSTEM/ADMIN_ADJUST',
    operator               VARCHAR(50)     NULL COMMENT '只有 ADJUST 有值，客服操作者帳號',
    created_at             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at             DATETIME        NULL COMMENT '只有 CONFIRMED 的 EARN 列才有值',
    related_transaction_id BIGINT UNSIGNED NULL COMMENT '只有 EXPIRE 列使用，指回被沖銷的 EARN 列 id',
    KEY idx_member_id (member_id),
    KEY idx_order_sn (order_sn)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
