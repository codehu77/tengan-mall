package com.tengan.mall.auth.application.port;

/**
 * OTP 安全設計照做（TTL、一次性核對、發送冷卻），刻意不接真實簡訊商——見
 * 微服務前台API待開發清單.md 第2節「開發細節：簡訊驗證碼」。
 */
public interface SmsCodeStorePort {

    /** 產生驗證碼並寫入 Redis（TTL 5 分鐘），觸發 60 秒發送冷卻；冷卻中會丟例外。 */
    String generateAndStore(String phone, String purpose);

    /**
     * 核對後即刪除（一次性），真正授權用途（例如 register）該呼叫這支，核對失敗回傳 false。
     * 不要跟 {@link #peek} 混用在同一次流程——同一組驗證碼只能被消耗一次。
     */
    boolean verifyAndConsume(String phone, String purpose, String code);

    /** 只核對不刪除，供 {@code /sms/verify} 這種 UX 預檢查用途，不是最終授權依據。 */
    boolean peek(String phone, String purpose, String code);
}
