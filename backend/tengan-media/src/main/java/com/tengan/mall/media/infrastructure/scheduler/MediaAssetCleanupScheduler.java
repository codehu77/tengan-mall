package com.tengan.mall.media.infrastructure.scheduler;

import com.tengan.mall.media.application.lifecycle.CleanupOrphanMediaAssetsUseCase;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 48 小時寬限期涵蓋交易競速窗口（sync 事件跟存檔之間的空窗）跟「使用者上傳後隔了一天才回來完成
 * 精靈」這種邊界情況，比照 tengan-wallet PointsExpiryScheduler 的排程慣例，interval 走 Nacos 可調。
 */
@Component
public class MediaAssetCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(MediaAssetCleanupScheduler.class);

    private final CleanupOrphanMediaAssetsUseCase cleanupOrphanMediaAssetsUseCase;
    private final int gracePeriodHours;

    public MediaAssetCleanupScheduler(CleanupOrphanMediaAssetsUseCase cleanupOrphanMediaAssetsUseCase,
            @Value("${tengan.media.orphan-grace-period-hours:48}") int gracePeriodHours) {
        this.cleanupOrphanMediaAssetsUseCase = cleanupOrphanMediaAssetsUseCase;
        this.gracePeriodHours = gracePeriodHours;
    }

    @Scheduled(fixedDelayString = "${tengan.media.cleanup-scan-interval-ms:3600000}")
    public void cleanupExpiredPending() {
        var cutoff = LocalDateTime.now().minusHours(gracePeriodHours);
        int deleted = cleanupOrphanMediaAssetsUseCase.cleanup(cutoff);
        if (deleted > 0) {
            log.info("媒體孤兒回收排程完成，本輪刪除 {} 筆", deleted);
        }
    }
}
