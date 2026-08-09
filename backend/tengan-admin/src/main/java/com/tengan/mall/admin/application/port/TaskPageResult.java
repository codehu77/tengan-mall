package com.tengan.mall.admin.application.port;

import java.util.List;

public record TaskPageResult(List<TaskItem> items, long total) {
}
