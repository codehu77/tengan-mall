package com.tengan.mall.payment.application.subscription;

import java.util.List;

/**
 * {@code QueryCreditCardPeriodInfo} 的完整查詢結果，供情境 B（ACTIVE 訂閱續期查帳）用——跟只取
 * 「首次授權」欄位的 {@link EcpayPeriodQueryResult}（情境 A 用）不同，這裡解析 {@code ExecLog[]}
 * 逐期明細，才能知道第二期以後各自有沒有扣款成功。
 *
 * @param execStatus       ECPay 原始 {@code ExecStatus}：0=已終止、1=執行中、2=執行完成。等於 0
 *                         時代表 ECPay 這邊已經不會再嘗試扣款，就算重播完 ExecLog 還是卡住，
 *                         也不用等下一輪排程，直接視同訂閱失效。
 * @param totalSuccessTimes ECPay 回報的累積成功次數，純供 log/debug 對照用，實際入帳以逐筆
 *                         {@link #entries} 重播為準，不直接使用這個累計值。
 */
public record EcpayPeriodExecLogQueryResult(String execStatus, int totalSuccessTimes,
        List<EcpayPeriodExecLogEntry> entries) {
}
