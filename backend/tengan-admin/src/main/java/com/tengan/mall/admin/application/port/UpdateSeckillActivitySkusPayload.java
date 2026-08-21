package com.tengan.mall.admin.application.port;

import java.util.List;

public record UpdateSeckillActivitySkusPayload(List<SeckillSkuItemPayload> items) {
}
