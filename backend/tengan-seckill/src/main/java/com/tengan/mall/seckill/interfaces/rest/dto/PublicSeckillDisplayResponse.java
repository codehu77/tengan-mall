package com.tengan.mall.seckill.interfaces.rest.dto;

import java.util.List;

public record PublicSeckillDisplayResponse(List<PublicFlashSaleSessionResponse> flashSaleSessions,
        List<PublicLaunchResponse> launches) {
}
