package com.tengan.mall.seckill.application.activity;

import java.util.List;

public record UpdateActivitySkusCommand(Long activityId, List<SkuItem> items) {
}
