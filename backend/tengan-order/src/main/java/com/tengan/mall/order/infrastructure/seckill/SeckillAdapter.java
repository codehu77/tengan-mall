package com.tengan.mall.order.infrastructure.seckill;

import com.tengan.mall.order.application.port.ActiveSeckillSku;
import com.tengan.mall.order.application.port.SeckillPort;
import com.tengan.mall.order.infrastructure.seckill.dto.ActiveSkuDto;
import com.tengan.mall.order.infrastructure.seckill.dto.BatchStatusRequestDto;
import com.tengan.mall.order.infrastructure.seckill.dto.BatchStatusResponseDto;
import com.tengan.mall.order.infrastructure.seckill.dto.SeckillReservationRequestDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SeckillAdapter implements SeckillPort {

    private final RestClient seckillRestClient;
    private final SeckillServiceTokenProvider tokenProvider;

    public SeckillAdapter(RestClient seckillRestClient, SeckillServiceTokenProvider tokenProvider) {
        this.seckillRestClient = seckillRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Map<Long, ActiveSeckillSku> checkActive(List<Long> skuIds) {
        BatchStatusResponseDto response = seckillRestClient.post()
                .uri("/internal/seckill/skus/batch-status")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new BatchStatusRequestDto(skuIds))
                .retrieve()
                .body(BatchStatusResponseDto.class);
        if (response == null) {
            throw new IllegalStateException("查詢秒殺狀態呼叫無回應: skuIds=" + skuIds);
        }
        return response.activeSkus().stream()
                .collect(Collectors.toMap(ActiveSkuDto::skuId,
                        dto -> new ActiveSeckillSku(dto.activityId(), dto.seckillPrice(), dto.limitPerUser())));
    }

    @Override
    public void reserve(Long skuId, Long memberId, int count) {
        seckillRestClient.post()
                .uri("/internal/seckill/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new SeckillReservationRequestDto(skuId, memberId, count))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void release(Long skuId, Long memberId, int count) {
        seckillRestClient.post()
                .uri("/internal/seckill/reservations/release")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new SeckillReservationRequestDto(skuId, memberId, count))
                .retrieve()
                .toBodilessEntity();
    }
}
