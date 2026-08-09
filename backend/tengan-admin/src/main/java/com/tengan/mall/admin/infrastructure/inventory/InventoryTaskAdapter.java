package com.tengan.mall.admin.infrastructure.inventory;

import com.tengan.mall.admin.application.port.InventoryTaskPort;
import com.tengan.mall.admin.application.port.TaskPageResult;
import com.tengan.mall.admin.infrastructure.inventory.dto.TaskListEnvelope;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class InventoryTaskAdapter implements InventoryTaskPort {

    private static final String BASE_PATH = "/internal/inventory/tasks";

    private final RestClient inventoryRestClient;
    private final InventoryServiceTokenProvider tokenProvider;

    public InventoryTaskAdapter(RestClient inventoryRestClient, InventoryServiceTokenProvider tokenProvider) {
        this.inventoryRestClient = inventoryRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public TaskPageResult listTasks(Integer status, String from, String to, int page, int pageSize) {
        String uri = UriComponentsBuilder.fromPath(BASE_PATH)
                .queryParamIfPresent("status", Optional.ofNullable(status))
                .queryParamIfPresent("from", Optional.ofNullable(from))
                .queryParamIfPresent("to", Optional.ofNullable(to))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build().toUriString();
        TaskListEnvelope envelope = inventoryRestClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(TaskListEnvelope.class);
        return envelope == null ? new TaskPageResult(List.of(), 0)
                : new TaskPageResult(envelope.items(), envelope.total());
    }
}
