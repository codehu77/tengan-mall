package com.tengan.mall.auth.application.port;

/**
 * 註冊成功後發布，tengan-member 消費後建立 profile row（非強一致性寫入走 MQ，
 * 見 docs/JWT設計.md 核心原則 5、微服務前台API待開發清單.md 第2節）。
 */
public interface MemberRegisteredEventPublisherPort {

    void publish(Long accountId, String username, String phone);
}
