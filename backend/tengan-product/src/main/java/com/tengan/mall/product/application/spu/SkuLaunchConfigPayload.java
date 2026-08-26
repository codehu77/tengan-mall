package com.tengan.mall.product.application.spu;

import java.time.LocalDateTime;

/**
 * saleStartTime 是 SPU 級欄位，同一個 SPU 底下所有 SKU 這個值相同。庫存流量閘門
 * （traffic_gate_enabled/gate_close_time）不在這裡——那是 tengan-inventory 自己直接管理的設定，
 * 不透過這個同步事件傳遞，見 Spu 類別的說明。
 */
public record SkuLaunchConfigPayload(Long skuId, LocalDateTime saleStartTime, Integer purchaseLimitPerUser) {
}
