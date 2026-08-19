package com.tengan.mall.payment.application.reconcile;

public interface TriggerReconcileNowUseCase {

    /**
     * 不管卡多久，立即把「目前所有還卡在 PENDING」的一般訂單 credit_card 記錄跟訂閱首期扣款
     * 全部查一次 ECPay 現況，供 tengan-admin「立即查帳」按鈕 demo 用（跳過排程原本的 40 分鐘門檻）。
     */
    ReconcileNowResult run();
}
