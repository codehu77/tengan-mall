package com.tengan.mall.order.infrastructure.seckill;

import com.tengan.mall.order.application.port.ActiveSeckillSku;
import com.tengan.mall.order.application.port.SeckillPort;
import com.tengan.mall.order.domain.exception.SeckillReservationFailedException;
import com.tengan.mall.order.infrastructure.seckill.dto.ActiveSkuDto;
import com.tengan.mall.order.infrastructure.seckill.dto.BatchStatusRequestDto;
import com.tengan.mall.order.infrastructure.seckill.dto.BatchStatusResponseDto;
import com.tengan.mall.order.infrastructure.seckill.dto.SeckillErrorDto;
import com.tengan.mall.order.infrastructure.seckill.dto.SeckillReservationRequestDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

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

    /**
     * 跟既有 {@code WalletAdapter.consume()} 不同的地方：這裡刻意攔截並翻譯例外，因為秒殺搶輸
     * 是結帳流程裡機率相對高、使用者需要看到清楚訊息的失敗情境（不是餘額不足那種邊角案例），
     * 翻譯成 {@link SeckillReservationFailedException} 讓 {@code OrderExceptionHandler} 能映射
     * 成 409 + 清楚訊息，見 Phase 9 前台整合規劃第 3 節。
     */
    @Override
    public void reserve(Long skuId, Long memberId, int count) {
        try {
            seckillRestClient.post()
                    .uri("/internal/seckill/reservations")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                    .body(new SeckillReservationRequestDto(skuId, memberId, count))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new SeckillReservationFailedException(skuId, extractMessage(e));
        }
    }

    private String extractMessage(RestClientResponseException e) {
        try {
            SeckillErrorDto body = e.getResponseBodyAs(SeckillErrorDto.class);
            if (body != null && body.message() != null) {
                return body.message();
            }
        } catch (RuntimeException ignored) {
            // 解析失敗就用預設訊息，不影響整筆訂單回滾的正確性
        }
        return "配額不足或活動已結束";
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
