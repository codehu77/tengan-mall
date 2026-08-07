package com.tengan.mall.admin.infrastructure.auth;

import com.tengan.mall.admin.application.port.AccountPort;
import com.tengan.mall.admin.application.port.AccountStatusItem;
import com.tengan.mall.admin.application.port.AccountStatusListResult;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AccountAdapter implements AccountPort {

    private static final String BASE_PATH = "/internal/accounts";

    private final RestClient authRestClient;
    private final AccountServiceTokenProvider tokenProvider;

    public AccountAdapter(RestClient authRestClient, AccountServiceTokenProvider tokenProvider) {
        this.authRestClient = authRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void disableAccount(Long accountId, String operatorToken) {
        authRestClient.put()
                .uri(BASE_PATH + "/{id}/disable", accountId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void enableAccount(Long accountId, String operatorToken) {
        authRestClient.put()
                .uri(BASE_PATH + "/{id}/enable", accountId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public List<AccountStatusItem> getStatuses(List<Long> accountIds) {
        if (accountIds.isEmpty()) {
            return List.of();
        }
        String ids = accountIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        AccountStatusListResult result = authRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("ids", ids).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(AccountStatusListResult.class);
        return result == null ? List.of() : result.items();
    }
}
