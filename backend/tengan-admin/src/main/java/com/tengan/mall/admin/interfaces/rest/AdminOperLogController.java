package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.operlog.SearchOperLogsQuery;
import com.tengan.mall.admin.application.operlog.SearchOperLogsUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.OperLogItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SearchOperLogsResponse;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system/logs")
public class AdminOperLogController {

    private final SearchOperLogsUseCase searchOperLogsUseCase;

    public AdminOperLogController(SearchOperLogsUseCase searchOperLogsUseCase) {
        this.searchOperLogsUseCase = searchOperLogsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:log:read')")
    public SearchOperLogsResponse search(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        var result = searchOperLogsUseCase.search(
                new SearchOperLogsQuery(adminId, module, from, to, pageNum, pageSize));
        var items = result.items().stream()
                .map(item -> new OperLogItemResponse(item.id(), item.adminUserId(), item.username(), item.module(),
                        item.action(), item.targetDesc(), item.resultStatus(), item.createdAt()))
                .toList();
        return new SearchOperLogsResponse(items, result.total());
    }
}
