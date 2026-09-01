package com.tengan.mall.auth.application.port;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * 一次性伺服器端驗證憑證（registrationToken/resetToken 共用同一套機制，之後 Phase 13 的
 * OAuth merge_token 也可以直接重用）：驗證通過後由伺服器發一個短期 opaque token，前端只負責
 * 原封不動把它帶到下一步，伺服器再查 Redis 核對存在且未過期，避免前端自帶身份資訊過去被竄改。
 */
public interface VerificationTokenStorePort {

    /** 產生 opaque token 存入 Redis，帶指定 TTL。 */
    String issue(Map<String, String> payload, Duration ttl);

    /** 核對成功即刪除（一次性），找不到/已過期回傳 empty。 */
    Optional<Map<String, String>> consume(String token);
}
