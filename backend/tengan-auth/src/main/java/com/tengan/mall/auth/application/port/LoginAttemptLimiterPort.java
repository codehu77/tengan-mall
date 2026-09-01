package com.tengan.mall.auth.application.port;

/**
 * 登入端點防暴力破解，仿照既有 OTP 冷卻/失敗計數的 Redis 模式：連續 5 次密碼錯誤鎖定該 identifier
 * 15 分鐘，鎖定期間直接拒絕，不再嘗試比對密碼。
 */
public interface LoginAttemptLimiterPort {

    boolean isLocked(String identifier);

    void recordFailure(String identifier);

    void reset(String identifier);
}
