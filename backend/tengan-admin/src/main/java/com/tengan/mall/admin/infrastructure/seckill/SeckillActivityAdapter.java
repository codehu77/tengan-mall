package com.tengan.mall.admin.infrastructure.seckill;

import com.tengan.mall.admin.application.port.CreateSeckillActivityPayload;
import com.tengan.mall.admin.application.port.SeckillActivityDetail;
import com.tengan.mall.admin.application.port.SeckillActivityItem;
import com.tengan.mall.admin.application.port.SeckillActivityPort;
import com.tengan.mall.admin.application.port.UpdateSeckillActivitySkusPayload;
import com.tengan.mall.admin.infrastructure.seckill.dto.ActivityListEnvelope;
import com.tengan.mall.admin.infrastructure.seckill.dto.IdEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SeckillActivityAdapter implements SeckillActivityPort {

    private static final String BASE_PATH = "/internal/seckill/activities";

    private final RestClient seckillRestClient;
    private final SeckillServiceTokenProvider tokenProvider;

    public SeckillActivityAdapter(RestClient seckillRestClient, SeckillServiceTokenProvider tokenProvider) {
        this.seckillRestClient = seckillRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<SeckillActivityItem> listActivities() {
        ActivityListEnvelope envelope = seckillRestClient.get()
                .uri(BASE_PATH + "?page=1&pageSize=100")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(ActivityListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public SeckillActivityDetail getActivity(Long id) {
        SeckillActivityDetail detail = seckillRestClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SeckillActivityDetail.class);
        if (detail == null) {
            throw new IllegalStateException("查詢秒殺活動呼叫無回應: id=" + id);
        }
        return detail;
    }

    @Override
    public Long createActivity(CreateSeckillActivityPayload payload) {
        IdEnvelope envelope = seckillRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        if (envelope == null) {
            throw new IllegalStateException("建立秒殺活動呼叫無回應");
        }
        return envelope.id();
    }

    @Override
    public void updateActivitySkus(Long id, UpdateSeckillActivitySkusPayload payload) {
        seckillRestClient.put()
                .uri(BASE_PATH + "/{id}/skus", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
