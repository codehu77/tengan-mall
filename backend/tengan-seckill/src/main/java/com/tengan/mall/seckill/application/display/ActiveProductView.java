package com.tengan.mall.seckill.application.display;

import java.util.List;

public record ActiveProductView(Long spuId, String name, String mainImage, List<ActiveSkuView> skus) {
}
