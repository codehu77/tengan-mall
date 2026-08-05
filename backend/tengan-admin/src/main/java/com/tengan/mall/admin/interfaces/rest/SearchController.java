package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.SearchReindexPort;
import com.tengan.mall.admin.interfaces.rest.dto.ReindexResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-search 的 /internal/search/reindex，純代理。 */
@RestController
@RequestMapping("/api/admin/search")
public class SearchController {

    private final SearchReindexPort searchReindexPort;

    public SearchController(SearchReindexPort searchReindexPort) {
        this.searchReindexPort = searchReindexPort;
    }

    @PostMapping("/reindex")
    @PreAuthorize("hasAuthority('search:reindex')")
    public ReindexResponse reindex() {
        return new ReindexResponse(searchReindexPort.reindex());
    }
}
