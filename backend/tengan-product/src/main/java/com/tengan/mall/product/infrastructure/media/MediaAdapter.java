package com.tengan.mall.product.infrastructure.media;

import com.tengan.mall.product.application.port.MediaPort;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MediaAdapter implements MediaPort {

    private final RestClient mediaRestClient;
    private final MediaServiceTokenProvider tokenProvider;

    public MediaAdapter(RestClient mediaRestClient, MediaServiceTokenProvider tokenProvider) {
        this.mediaRestClient = mediaRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void deleteImages(List<String> urls) {
        if (urls.isEmpty()) {
            return;
        }
        mediaRestClient.method(HttpMethod.DELETE)
                .uri("/internal/media/objects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(Map.of("urls", urls))
                .retrieve()
                .toBodilessEntity();
    }
}
