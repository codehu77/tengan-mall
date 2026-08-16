package com.tengan.mall.admin.application.port;

/** 呼叫 tengan-wallet 的行銷管理 internal 端點，跟 {@link PaymentPort} 同樣的純代理原則（不重做業務規則）。 */
public interface WalletPort {

    WalletRuleItem getRules();

    void updateRules(WalletRuleItem rule, String operatorToken);

    void adjustPoints(Long memberId, int points, String reason, String operatorToken);

    void updateTier(Long memberId, String tier, String reason, String operatorToken);
}
