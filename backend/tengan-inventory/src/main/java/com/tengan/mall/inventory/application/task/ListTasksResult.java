package com.tengan.mall.inventory.application.task;

import java.util.List;

public record ListTasksResult(List<WareOrderTaskQueryItem> items, long total) {
}
