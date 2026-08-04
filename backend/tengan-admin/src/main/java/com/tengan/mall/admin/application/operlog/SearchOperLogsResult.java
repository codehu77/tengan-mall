package com.tengan.mall.admin.application.operlog;

import java.util.List;

public record SearchOperLogsResult(List<OperLogItem> items, long total) {
}
