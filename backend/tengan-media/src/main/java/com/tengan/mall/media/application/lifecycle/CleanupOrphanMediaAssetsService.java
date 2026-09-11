package com.tengan.mall.media.application.lifecycle;

import com.tengan.mall.media.application.port.FileStoragePort;
import com.tengan.mall.media.domain.repository.MediaAssetRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 查詢用有索引的 (status, updated_at) 條件縮小到「還沒被確認使用的少數候選」，不對 MinIO 做全量
 * 列物件掃描——成本只跟候選數量成正比，不隨全站物件總量增加。排程（寬限期 cutoff）跟後台手動觸發
 * （cutoff=now）共用同一支。
 */
@Service
public class CleanupOrphanMediaAssetsService implements CleanupOrphanMediaAssetsUseCase {

    private static final Logger log = LoggerFactory.getLogger(CleanupOrphanMediaAssetsService.class);
    private static final int BATCH_LIMIT = 200;

    private final MediaAssetRepository mediaAssetRepository;
    private final FileStoragePort fileStoragePort;

    public CleanupOrphanMediaAssetsService(MediaAssetRepository mediaAssetRepository,
            FileStoragePort fileStoragePort) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    public int cleanup(LocalDateTime cutoff) {
        var candidates = mediaAssetRepository.findExpiredPending(cutoff, BATCH_LIMIT);
        int deleted = 0;
        for (var asset : candidates) {
            try {
                fileStoragePort.deleteIfOwned(asset.getUrl());
                mediaAssetRepository.delete(asset.getId());
                deleted++;
            } catch (RuntimeException e) {
                log.error("回收孤兒媒體物件失敗，mediaAssetId={}, objectKey={}，等下一輪重試", asset.getId(),
                        asset.getObjectKey(), e);
            }
        }
        return deleted;
    }
}
