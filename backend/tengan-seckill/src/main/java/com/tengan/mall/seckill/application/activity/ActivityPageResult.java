package com.tengan.mall.seckill.application.activity;

import java.util.List;

public record ActivityPageResult(List<ActivityView> items, long total) {
}
