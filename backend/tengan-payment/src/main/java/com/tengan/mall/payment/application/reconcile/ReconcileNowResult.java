package com.tengan.mall.payment.application.reconcile;

/**
 * 「立即查帳」單次執行的結果統計，供 tengan-admin 顯示。converged+failed 可能小於 checked——
 * 中間若有單筆查詢 ECPay 失敗（例如網路問題），會被記錄但不計入 converged/failed，等下一次
 * 排程或再按一次「立即查帳」重試。
 */
public record ReconcileNowResult(int paymentChecked, int paymentConverged, int paymentFailed,
        int subscriptionChecked, int subscriptionConverged, int subscriptionFailed, int renewalChecked,
        int renewalRecovered) {
}
