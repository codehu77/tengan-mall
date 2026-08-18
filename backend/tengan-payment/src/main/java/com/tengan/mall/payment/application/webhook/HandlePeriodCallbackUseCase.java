package com.tengan.mall.payment.application.webhook;

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
}
