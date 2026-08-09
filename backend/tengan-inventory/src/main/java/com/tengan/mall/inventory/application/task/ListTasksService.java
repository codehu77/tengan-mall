package com.tengan.mall.inventory.application.task;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class ListTasksService implements ListTasksUseCase {

    private final WareOrderTaskQueryPort wareOrderTaskQueryPort;

    public ListTasksService(WareOrderTaskQueryPort wareOrderTaskQueryPort) {
        this.wareOrderTaskQueryPort = wareOrderTaskQueryPort;
    }

    @Override
    public ListTasksResult list(Integer status, Instant from, Instant to, int pageNum, int pageSize) {
        var items = wareOrderTaskQueryPort.search(status, from, to, pageNum, pageSize);
        long total = wareOrderTaskQueryPort.countSearch(status, from, to);
        return new ListTasksResult(items, total);
    }
}
