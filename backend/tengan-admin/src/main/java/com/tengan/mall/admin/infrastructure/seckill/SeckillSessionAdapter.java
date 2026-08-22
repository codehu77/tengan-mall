package com.tengan.mall.admin.infrastructure.seckill;

import com.tengan.mall.admin.application.port.SeckillSessionItem;
import com.tengan.mall.admin.application.port.SeckillSessionPayload;
import com.tengan.mall.admin.application.port.SeckillSessionPort;
import com.tengan.mall.admin.infrastructure.seckill.dto.IdEnvelope;
import com.tengan.mall.admin.infrastructure.seckill.dto.SessionListEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SeckillSessionAdapter implements SeckillSessionPort {

    private static final String BASE_PATH = "/internal/seckill/sessions";

    private final RestClient seckillRestClient;
    private final SeckillServiceTokenProvider tokenProvider;

    public SeckillSessionAdapter(RestClient seckillRestClient, SeckillServiceTokenProvider tokenProvider) {
        this.seckillRestClient = seckillRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<SeckillSessionItem> listSessions() {
        SessionListEnvelope envelope = seckillRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SessionListEnvelope.class);
        return envelope == null ? List.of() : envelope.sessions();
    }

    @Override
    public Long createSession(SeckillSessionPayload payload) {
        IdEnvelope envelope = seckillRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        if (envelope == null) {
            throw new IllegalStateException("建立秒殺場次呼叫無回應");
        }
        return envelope.id();
    }

    @Override
    public void updateSession(Long id, SeckillSessionPayload payload) {
        seckillRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteSession(Long id) {
        seckillRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .toBodilessEntity();
    }
}
