CREATE TABLE admin_user (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    real_name     VARCHAR(50)  NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=ACTIVE 0=DISABLED',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admin_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE role (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_code  VARCHAR(50)  NOT NULL,
    role_name  VARCHAR(50)  NOT NULL,
    status     TINYINT      NOT NULL DEFAULT 1 COMMENT '1=ACTIVE 0=DISABLED',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_role_code (role_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE menu (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    parent_id       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0=root，無實體 FK 指向自己',
    menu_type       TINYINT      NOT NULL COMMENT '1=CATALOG 2=MENU 3=BUTTON',
    title           VARCHAR(50)  NOT NULL,
    path            VARCHAR(200) NULL,
    component       VARCHAR(200) NULL,
    route_name      VARCHAR(100) NULL COMMENT '前端路由 name，keep-alive/去重用',
    icon            VARCHAR(100) NULL,
    permission_code VARCHAR(100) NULL COMMENT 'colon 式，例如 system:user:write，跟 OAuth2 scope 的 dot 式不同',
    sort_order      INT          NOT NULL DEFAULT 0,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=ACTIVE 0=DISABLED',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE role_menu (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_id    BIGINT UNSIGNED NOT NULL,
    menu_id    BIGINT UNSIGNED NOT NULL,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE user_role (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admin_user_id BIGINT UNSIGNED NOT NULL,
    role_id       BIGINT UNSIGNED NOT NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (admin_user_id, role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE oper_log (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admin_user_id BIGINT UNSIGNED NOT NULL,
    username      VARCHAR(50)  NOT NULL COMMENT '操作當下的帳號快照，避免帳號改名/停用後查不到是誰做的',
    module        VARCHAR(50)  NOT NULL,
    action        VARCHAR(50)  NOT NULL,
    target_desc   VARCHAR(200) NULL,
    result_status TINYINT      NOT NULL COMMENT '1=成功 0=拒絕/失敗',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_oper_log_admin_user (admin_user_id),
    KEY idx_oper_log_module (module)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;
