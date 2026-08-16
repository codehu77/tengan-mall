package com.tengan.mall.order.infrastructure.wallet;

import com.tengan.mall.order.application.port.PointsConsumeResult;
import com.tengan.mall.order.application.port.WalletPort;
import com.tengan.mall.order.infrastructure.wallet.dto.ConsumePointsRequestDto;
import com.tengan.mall.order.infrastructure.wallet.dto.ConsumePointsResponseDto;
import com.tengan.mall.order.infrastructure.wallet.dto.EarnRequestDto;
import com.tengan.mall.order.infrastructure.wallet.dto.ReserveRequestDto;
import com.tengan.mall.order.infrastructure.wallet.dto.RevertPointsRequestDto;
import com.tengan.mall.order.infrastructure.wallet.dto.WalletRuleResponseDto;
import java.math.BigDecimal;
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
    public void reserve(Long memberId, String orderSn, BigDecimal payAmount) {
        walletRestClient.post()
                .uri("/internal/wallet/points/reserve")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new ReserveRequestDto(memberId, orderSn, payAmount))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void earn(Long memberId, String orderSn, BigDecimal payAmount) {
        walletRestClient.post()
                .uri("/internal/wallet/points/earn")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new EarnRequestDto(memberId, orderSn, payAmount))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public PointsConsumeResult consume(Long memberId, int points, String orderSn) {
        ConsumePointsResponseDto response = walletRestClient.post()
                .uri("/internal/wallet/points/consume")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new ConsumePointsRequestDto(memberId, points, orderSn))
                .retrieve()
                .body(ConsumePointsResponseDto.class);
        if (response == null) {
            throw new IllegalStateException("核銷點數呼叫無回應: memberId=" + memberId + " orderSn=" + orderSn);
        }
        return new PointsConsumeResult(response.discountAmount());
    }

    @Override
    public void revert(Long memberId, String orderSn) {
        walletRestClient.post()
                .uri("/internal/wallet/points/revert")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new RevertPointsRequestDto(memberId, orderSn))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public int getGracePeriodMinutes() {
        WalletRuleResponseDto response = walletRestClient.get()
                .uri("/internal/wallet/rules")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(WalletRuleResponseDto.class);
        if (response == null) {
            throw new IllegalStateException("查詢 wallet_rule 呼叫無回應");
        }
        return response.gracePeriodMinutes();
    }
}
