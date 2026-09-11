package com.tengan.mall.media.interfaces.rest;

import com.tengan.mall.media.application.lifecycle.CleanupOrphanMediaAssetsUseCase;
import com.tengan.mall.media.interfaces.rest.dto.CleanupMediaAssetsResponse;
import java.time.LocalDateTime;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 供 tengan-admin BFF 轉發，後台「立即清理孤兒圖片」按鈕用——跟排程共用同一支 use case，
 * 差別只在 cutoff 傳「現在」不等寬限期。 */
@RestController
@RequestMapping("/internal/media/assets")
public class InternalMediaAssetController {

    private final CleanupOrphanMediaAssetsUseCase cleanupOrphanMediaAssetsUseCase;

    public InternalMediaAssetController(CleanupOrphanMediaAssetsUseCase cleanupOrphanMediaAssetsUseCase) {
        this.cleanupOrphanMediaAssetsUseCase = cleanupOrphanMediaAssetsUseCase;
    }

    @PostMapping("/cleanup-now")
    @PreAuthorize("hasAuthority('SCOPE_media.write')")
    public CleanupMediaAssetsResponse cleanupNow() {
        int deleted = cleanupOrphanMediaAssetsUseCase.cleanup(LocalDateTime.now());
        return new CleanupMediaAssetsResponse(deleted);
    }
}
