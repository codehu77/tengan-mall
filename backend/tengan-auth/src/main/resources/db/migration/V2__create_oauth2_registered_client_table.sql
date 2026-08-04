-- Spring Security 官方 Spring Authorization Server 固定 schema（JdbcRegisteredClientRepository
-- 內建的 SQL 直接寫死表名/欄位名），不套用本專案自訂表命名規範（docs/資料庫設計規範.md）——
-- 這張表由函式庫擁有，不是我們自己的網域表。client_settings/token_settings 內容一律透過
-- RegisteredClientSeeder 用官方 builder API 寫入，不要手刻 INSERT。
CREATE TABLE oauth2_registered_client (
    id                              VARCHAR(100)  NOT NULL,
    client_id                       VARCHAR(100)  NOT NULL,
    client_id_issued_at             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_secret                   VARCHAR(200)  DEFAULT NULL,
    client_secret_expires_at        TIMESTAMP     NULL DEFAULT NULL,
    client_name                     VARCHAR(200)  NOT NULL,
    client_authentication_methods   VARCHAR(1000) NOT NULL,
    authorization_grant_types       VARCHAR(1000) NOT NULL,
    redirect_uris                   VARCHAR(1000) DEFAULT NULL,
    post_logout_redirect_uris       VARCHAR(1000) DEFAULT NULL,
    scopes                          VARCHAR(1000) NOT NULL,
    client_settings                 VARCHAR(2000) NOT NULL,
    token_settings                  VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
