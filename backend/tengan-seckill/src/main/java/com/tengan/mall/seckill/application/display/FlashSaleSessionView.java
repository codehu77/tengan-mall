package com.tengan.mall.seckill.application.display;

import java.time.Instant;
import java.util.List;

/** status 是 "ACTIVE"（現正瘋搶）或 "PUBLISHED"（準時開搶，尚未預熱）——前台依此決定要不要顯示倒數/開放下單。 */
public record FlashSaleSessionView(Long activityId, Long sessionId, String sessionName, Instant startTime,
        Instant endTime, String status, List<ActiveSkuView> skus) {
}
