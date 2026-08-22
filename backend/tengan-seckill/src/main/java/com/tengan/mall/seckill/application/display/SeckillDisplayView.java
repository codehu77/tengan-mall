package com.tengan.mall.seckill.application.display;

import java.util.List;

public record SeckillDisplayView(List<FlashSaleSessionView> flashSaleSessions, List<LaunchView> launches) {
}
