package com.tengan.mall.order.application.port;

import java.math.BigDecimal;

/**
 * 呼叫 tengan-wallet 的 internal 端點。reserve/earn 是點數「入帳」路徑（訂單確認收貨/鑑賞期排程），
 * consume/revert 是點數「折抵」路徑（下單 Saga/取消訂單），兩條路徑互不影響。
 */
public interface WalletPort {

    /** 訂單確認收貨當下的非關鍵路徑呼叫，失敗不影響確認收貨本身（呼叫端自行 safely() 包裹）。 */
    void reserve(Long memberId, String orderSn, BigDecimal payAmount);

    /** 鑑賞期過後由 PointsGrantScheduler 呼叫，冪等。 */
    void earn(Long memberId, String orderSn, BigDecimal payAmount);

    /** 下單 Saga 呼叫，回傳伺服器算出的折抵金額（不信任前端送來的金額）。 */
    PointsConsumeResult consume(Long memberId, int points, String orderSn);

    /** 取消訂單三條路徑（顧客取消/逾時取消/客服代取消）呼叫，冪等 no-op 安全。 */
    void revert(Long memberId, String orderSn);

    /** 鑑賞期分鐘數是 wallet_rule 的 DB 欄位（可後台即時調整），排程每次掃描前現查，不快取。 */
    int getGracePeriodMinutes();
}
