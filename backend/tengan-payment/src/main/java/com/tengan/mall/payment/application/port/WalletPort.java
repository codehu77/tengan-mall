package com.tengan.mall.payment.application.port;

public interface WalletPort {

    /** 呼叫 tengan-wallet 的系統觸發升等端點（不需要 X-Identity-Assertion，系統觸發沒有人類操作者）。 */
    void upgradeTier(Long memberId, String tier, String reason);

    /** 內部一樣呼叫升等端點，只是 tier 固定傳 FREE。 */
    void downgradeTier(Long memberId, String reason);
}
