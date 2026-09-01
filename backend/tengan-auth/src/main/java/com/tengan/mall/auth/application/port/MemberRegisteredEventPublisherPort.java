package com.tengan.mall.auth.application.port;

/**
 * 註冊成功後發布，tengan-member 消費後建立 profile row（非強一致性寫入走 MQ，
 * 見 docs/JWT設計.md 核心原則 5、微服務前台API待開發清單.md 第2節）。
 * nickname/avatarUrl 只有 OAuth 註冊才會給值（用 provider 回傳的個人資料寫入一次，見
 * oauth_login_design 定案），密碼註冊傳 null/null，tengan-member 端退回原本的預設值邏輯。
 */
public interface MemberRegisteredEventPublisherPort {

    void publish(Long accountId, String phone, String email, String nickname, String avatarUrl);
}
