package com.tengan.mall.admin.infrastructure.seckill.dto;

import com.tengan.mall.admin.application.port.SeckillActivityItem;
import java.util.List;

public record ActivityListEnvelope(List<SeckillActivityItem> items, long total) {
}
