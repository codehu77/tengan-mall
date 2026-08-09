package com.tengan.mall.inventory.application.task;

import java.time.Instant;

public interface ListTasksUseCase {

    ListTasksResult list(Integer status, Instant from, Instant to, int pageNum, int pageSize);
}
