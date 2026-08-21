package com.tengan.mall.seckill.interfaces.rest.dto;

import java.util.List;

public record BatchStatusResponse(List<ActiveSkuResponse> activeSkus) {
}
