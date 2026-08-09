package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record ListTasksResponse(List<TaskItemResponse> items, long total) {
}
