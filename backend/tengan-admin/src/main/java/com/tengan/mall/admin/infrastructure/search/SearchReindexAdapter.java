package com.tengan.mall.admin.infrastructure.search;

import com.tengan.mall.admin.application.port.SearchReindexPort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SearchReindexAdapter implements SearchReindexPort {

    private static final String PATH = "/internal/search/reindex";

    private final RestClient searchRestClient;
    private final SearchServiceTokenProvider tokenProvider;

    public SearchReindexAdapter(RestClient searchRestClient, SearchServiceTokenProvider tokenProvider) {
        this.searchRestClient = searchRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public int reindex() {
        ReindexResult result = searchRestClient.post()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(ReindexResult.class);
        return result == null ? 0 : result.indexedCount();
    }
}
