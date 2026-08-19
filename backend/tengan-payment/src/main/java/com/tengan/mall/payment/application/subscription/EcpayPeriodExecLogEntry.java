package com.tengan.mall.payment.application.subscription;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * ECPay {@code QueryCreditCardPeriodInfo} 回應裡 {@code ExecLog[]} 的單筆執行紀錄——跟頂層欄位不同，
 * 這裡每一筆都是「這一期」自己的資料（頂層 process_date/gwsr/amount 官方文件明講是「首次授權」專屬，
 * 不能拿來代表最新一期，見 {@link EcpayPeriodExecLogQueryResult} javadoc）。
 */
public record EcpayPeriodExecLogEntry(boolean success, BigDecimal amount, String gwsr, Instant processDate,
        String tradeNo) {
}
