package com.tengan.mall.search.interfaces.rest;

import com.tengan.mall.search.application.ReindexAllUseCase;
import com.tengan.mall.search.interfaces.rest.dto.ReindexResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/search")
public class InternalSearchController {

    private final ReindexAllUseCase reindexAllUseCase;

    public InternalSearchController(ReindexAllUseCase reindexAllUseCase) {
        this.reindexAllUseCase = reindexAllUseCase;
    }

    @PostMapping("/reindex")
    @PreAuthorize("hasAuthority('SCOPE_search.write')")
    public ReindexResponse reindex() {
        return new ReindexResponse(reindexAllUseCase.reindexAll());
    }
}
