package com.tengan.mall.admin.infrastructure.wallet;

import com.tengan.mall.admin.application.port.WalletPort;
import com.tengan.mall.admin.application.port.WalletRuleItem;
import com.tengan.mall.admin.infrastructure.wallet.dto.AdjustPointsPayload;
import com.tengan.mall.admin.infrastructure.wallet.dto.UpdateTierPayload;
import com.tengan.mall.admin.infrastructure.wallet.dto.WalletRuleDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WalletAdapter implements WalletPort {

    private final RestClient walletRestClient;
    private final WalletServiceTokenProvider tokenProvider;

    public WalletAdapter(RestClient walletRestClient, WalletServiceTokenProvider tokenProvider) {
        this.walletRestClient = walletRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public WalletRuleItem getRules() {
        WalletRuleDto dto = walletRestClient.get().uri("/internal/wallet/rules")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .body(WalletRuleDto.class);
        if (dto == null) {
            throw new IllegalStateException("查詢點數規則呼叫無回應");
        }
        return new WalletRuleItem(dto.cashbackRatePro(), dto.cashbackRateProPlus(), dto.monthlyCapPro(),
                dto.monthlyCapProPlus(), dto.pointExpiryDays(), dto.gracePeriodMinutes(), dto.pointValueRatio());
    }

    @Override
    public void updateRules(WalletRuleItem rule, String operatorToken) {
        walletRestClient.put().uri("/internal/wallet/rules")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(new WalletRuleDto(rule.cashbackRatePro(), rule.cashbackRateProPlus(), rule.monthlyCapPro(),
                        rule.monthlyCapProPlus(), rule.pointExpiryDays(), rule.gracePeriodMinutes(),
                        rule.pointValueRatio()))
                .retrieve().toBodilessEntity();
    }

    @Override
    public void adjustPoints(Long memberId, int points, String reason, String operatorToken) {
        walletRestClient.post().uri("/internal/wallet/points/adjust")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(new AdjustPointsPayload(memberId, points, reason))
                .retrieve().toBodilessEntity();
    }

    @Override
    public void updateTier(Long memberId, String tier, String reason, String operatorToken) {
        walletRestClient.post().uri("/internal/wallet/members/{memberId}/tier", memberId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(new UpdateTierPayload(tier, reason))
                .retrieve().toBodilessEntity();
    }
}
