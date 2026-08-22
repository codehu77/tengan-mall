package com.tengan.mall.admin.infrastructure.seckill.dto;

import com.tengan.mall.admin.application.port.SeckillSessionItem;
import java.util.List;

public record SessionListEnvelope(List<SeckillSessionItem> sessions) {
}
