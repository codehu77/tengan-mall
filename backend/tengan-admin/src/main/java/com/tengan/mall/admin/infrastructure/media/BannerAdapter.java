package com.tengan.mall.admin.infrastructure.media;

import com.tengan.mall.admin.application.port.BannerItem;
import com.tengan.mall.admin.application.port.BannerPayload;
import com.tengan.mall.admin.application.port.BannerPort;
import com.tengan.mall.admin.infrastructure.media.dto.BannerListEnvelope;
import com.tengan.mall.admin.infrastructure.media.dto.IdEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BannerAdapter implements BannerPort {

    private static final String BASE_PATH = "/internal/media/banners";

    private final RestClient mediaRestClient;
    private final MediaServiceTokenProvider tokenProvider;

    public BannerAdapter(RestClient mediaRestClient, MediaServiceTokenProvider tokenProvider) {
        this.mediaRestClient = mediaRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<BannerItem> listBanners() {
        BannerListEnvelope envelope = mediaRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(BannerListEnvelope.class);
        return envelope == null ? List.of() : envelope.banners();
    }

    @Override
    public Long createBanner(BannerPayload payload) {
        IdEnvelope envelope = mediaRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        if (envelope == null) {
            throw new IllegalStateException("建立輪播圖呼叫無回應");
        }
        return envelope.id();
    }

    @Override
    public void updateBanner(Long id, BannerPayload payload) {
        mediaRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteBanner(Long id) {
        mediaRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .toBodilessEntity();
    }
}
