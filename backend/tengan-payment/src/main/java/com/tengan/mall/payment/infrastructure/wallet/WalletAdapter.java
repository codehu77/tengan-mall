package com.tengan.mall.payment.infrastructure.wallet;

import com.tengan.mall.payment.application.port.WalletPort;
import com.tengan.mall.payment.infrastructure.wallet.dto.UpdateTierPayload;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 呼叫 tengan-wallet 的系統觸發升等端點——沒有 X-Identity-Assertion，因為這是系統觸發（訂閱扣款
 * 成功/到期）沒有人類操作者，比照 tengan-order 呼叫 tengan-payment 沒有人類操作者的
 * {@code /internal/orders/{orderSn}/paid} 端點同一種模式。
 */
@Component
public class WalletAdapter implements WalletPort {

    private final RestClient walletRestClient;
    private final WalletServiceTokenProvider tokenProvider;

    public WalletAdapter(RestClient walletRestClient, WalletServiceTokenProvider tokenProvider) {
        this.walletRestClient = walletRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void upgradeTier(Long memberId, String tier, String reason) {
        updateTier(memberId, tier, reason);
    }

    @Override
    public void downgradeTier(Long memberId, String reason) {
        updateTier(memberId, "FREE", reason);
    }

    private void updateTier(Long memberId, String tier, String reason) {
        walletRestClient.post().uri("/internal/wallet/members/{memberId}/tier/subscription", memberId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new UpdateTierPayload(tier, reason))
                .retrieve()
                .toBodilessEntity();
    }
}
