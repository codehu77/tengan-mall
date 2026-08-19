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
}
