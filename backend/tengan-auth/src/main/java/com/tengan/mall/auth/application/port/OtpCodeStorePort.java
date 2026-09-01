package com.tengan.mall.auth.application.port;

/**
 * OTP 安全設計照做（TTL、一次性核對、發送冷卻、失敗次數上限），刻意不接真實簡訊/郵件商——見
 * 微服務前台API待開發清單.md 第2節「開發細節：簡訊驗證碼」。identifier 可以是 phone 或 email，
 * Redis key scheme 本來就跟格式無關，phone/email 共用同一套邏輯，不重複實作。
 */
public interface OtpCodeStorePort {

    /** 產生驗證碼並寫入 Redis（TTL 5 分鐘），觸發 60 秒發送冷卻；冷卻中會丟例外。 */
    String generateAndStore(String identifier, String purpose);

    /**
     * 核對成功即刪除（一次性）並回傳 true；核對失敗回傳 false 並累計失敗次數，
     * 累計達 5 次時該組驗證碼直接作廢（連同失敗計數一併清除），丟 {@code TooManyOtpAttemptsException}
     * 逼使用者重新發送，而不是回傳 false 讓呼叫端誤以為還能繼續猜。
     */
    boolean verifyAndConsume(String identifier, String purpose, String code);
}
