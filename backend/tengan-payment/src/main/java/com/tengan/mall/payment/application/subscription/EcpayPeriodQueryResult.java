package com.tengan.mall.payment.application.subscription;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * ECPay {@code QueryCreditCardPeriodInfo} 查詢結果，只取「首次授權」相關欄位——同步查帳目前只用來
 * 確認訂閱首期到底有沒有成功（Phase 8.6 擴充的使用情境），不處理第二期以後的查詢。
 * {@code firstPeriodSucceeded}：{@code RtnCode=1} 且 {@code TotalSuccessTimes>=1} 才算首期成功。
 * {@code gwsr} 直接沿用 ECPay 首次授權的交易序號，跟 {@link com.tengan.mall.payment.application.webhook.HandlePeriodCallbackService}
 * 的 {@code gwsr} 冪等鍵用同一個值，萬一之後遲到的真實 {@code PeriodReturnURL} 通知還是送達，能被正確認出重複。
 */
public record EcpayPeriodQueryResult(boolean firstPeriodSucceeded, String gwsr, String tradeNo, BigDecimal amount,
        int totalSuccessTimes, Instant processDate) {
}
