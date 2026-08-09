package com.tengan.mall.inventory.interfaces.rest.dto;

import java.util.List;

public record ListTasksResponse(List<WareOrderTaskResponse> items, long total) {
}
