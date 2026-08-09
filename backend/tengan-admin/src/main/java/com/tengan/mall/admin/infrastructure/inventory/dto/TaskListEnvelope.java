package com.tengan.mall.admin.infrastructure.inventory.dto;

import com.tengan.mall.admin.application.port.TaskItem;
import java.util.List;

public record TaskListEnvelope(List<TaskItem> items, long total) {
}
