package com.tengan.mall.payment.application.webhook;

import java.math.BigDecimal;
import java.time.Instant;

public interface HandlePeriodCallbackUseCase {

    void handle(HandlePeriodCallbackCommand command);

    /**
     * 定期定額第一期的授權結果，ECPay 在 AioCheckOut 當下就透過訂閱專屬的 ReturnURL 同步送回來了，
     * 不像第二期以後要等批次的 PeriodReturnURL 通知（可能延遲到隔天）。由
     * {@link HandleSubscriptionReturnCallbackService} 驗簽、確認 RtnCode=1 後呼叫。
     *
     * @param amountRaw    ReturnURL 的 TradeAmt 欄位原始字串
     * @param tradeDateRaw ReturnURL 的 TradeDate 欄位原始字串，格式同 PeriodReturnURL 的 ProcessDate
     */
    void confirmFirstPeriodFromReturnUrl(String merchantTradeNo, String tradeNo, String amountRaw,
            String tradeDateRaw);

    /**
     * 同步查帳（使用者重試訂閱時觸發/排程掃描）查到「首期其實已經授權成功」時呼叫，跟
     * {@link #confirmFirstPeriodFromReturnUrl} 屬於同一組「確認首期成功」語意，差別只在觸發來源跟冪等鍵
     * ——這裡直接用查詢回應真正的 gwsr（不是合成字串），好處是萬一 PeriodReturnURL 之後還是遲到送達，
     * 既有的 existsByGwsr 冪等檢查能正確認出同一筆、不重複入帳。
     */
    void confirmFromPeriodQuery(String merchantTradeNo, String gwsr, BigDecimal amount, int totalSuccessTimes,
            Instant processDate);

    /**
     * 情境 B（ACTIVE 訂閱續期查帳）用：把 ExecLog 裡的一筆執行紀錄（可能成功可能失敗）餵回既有的
     * 收斂邏輯，跟 PeriodReturnURL 通知走同一套處理——成功才延長 paidUntil/升級，失敗只累計連續失敗
     * 次數（累計到 6 次一樣會自動轉 CANCELLED，跟 webhook 路徑共用同一段判斷，不重寫）。gwsr 直接沿用
     * ECPay 回傳值，既有的 existsByGwsr 冪等檢查會自然擋掉已經處理過的紀錄（不管是先前 webhook 補上
     * 還是查帳查過），可以對整份 ExecLog 每筆都無腦重播一次，不用自己先判斷哪些是新的。
     */
    void replayPeriodResult(String merchantTradeNo, String gwsr, boolean success, BigDecimal amount,
            int totalSuccessTimes, Instant processDate);
}
