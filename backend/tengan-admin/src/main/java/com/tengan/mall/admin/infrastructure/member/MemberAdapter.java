package com.tengan.mall.admin.infrastructure.member;

import com.tengan.mall.admin.application.port.MemberItem;
import com.tengan.mall.admin.application.port.MemberListResult;
import com.tengan.mall.admin.application.port.MemberPort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MemberAdapter implements MemberPort {

    private static final String BASE_PATH = "/internal/members";

    private final RestClient memberRestClient;
    private final MemberServiceTokenProvider tokenProvider;

    public MemberAdapter(RestClient memberRestClient, MemberServiceTokenProvider tokenProvider) {
        this.memberRestClient = memberRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public MemberListResult listMembers(String keyword, int pageNum, int pageSize) {
        return memberRestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(BASE_PATH).queryParam("pageNum", pageNum).queryParam("pageSize", pageSize);
                    if (keyword != null && !keyword.isBlank()) {
                        uriBuilder.queryParam("keyword", keyword);
                    }
                    return uriBuilder.build();
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(MemberListResult.class);
    }

    @Override
    public MemberItem getMember(Long id) {
        return memberRestClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(MemberItem.class);
    }
}
