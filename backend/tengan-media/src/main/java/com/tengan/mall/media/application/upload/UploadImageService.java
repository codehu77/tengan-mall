package com.tengan.mall.media.application.upload;

import com.tengan.mall.media.application.port.FileStoragePort;
import com.tengan.mall.media.domain.exception.FileTooLargeException;
import com.tengan.mall.media.domain.exception.UnsupportedCategoryException;
import com.tengan.mall.media.domain.exception.UnsupportedFileTypeException;
import com.tengan.mall.media.domain.model.MediaAsset;
import com.tengan.mall.media.domain.repository.MediaAssetRepository;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * category 決定物件儲存的 key 前綴，同時也是白名單防呆——customer 端只允許 avatar（會員頭像），
 * internal 端允許 banner/product（後台素材）+ admin-avatar（管理員自己的頭像，tengan-admin
 * 轉發），各自在 Controller 層限制傳入值，這裡再做一次全域白名單兜底（比照 tengan-admin 既有
 * UploadFileService 的檔案型別/大小驗證慣例）。有 ownerId 時（會員/管理員頭像都會帶自己的 id）
 * 額外用 "{category}/{ownerId}" 當前綴，避免同一 category 底下全部使用者的檔案扁平混在一起。
 */
@Service
public class UploadImageService implements UploadImageUseCase {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("avatar", "banner", "product", "admin-avatar",
            "brand");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_BYTES = 2L * 1024 * 1024;

    /**
     * 生命週期追蹤（media_asset）目前只有 "product"（tengan-product 的 SPU/SKU）有對應的 sync
     * 事件會把 PENDING 轉成 CONFIRMED——avatar/banner/brand/admin-avatar 還沒有呼叫端會做這個
     * 確認動作，若無條件幫所有 category 都建追蹤列，這些從未被確認過的圖會在寬限期過後被
     * MediaAssetCleanupScheduler 誤判成孤兒清掉。之後要擴充哪個 category，同時要把它的存檔
     * 路徑接上 sync 事件，兩件事必須一起做。
     */
    private static final Set<String> LIFECYCLE_TRACKED_CATEGORIES = Set.of("product");

    private final FileStoragePort fileStoragePort;
    private final MediaAssetRepository mediaAssetRepository;

    public UploadImageService(FileStoragePort fileStoragePort, MediaAssetRepository mediaAssetRepository) {
        this.fileStoragePort = fileStoragePort;
        this.mediaAssetRepository = mediaAssetRepository;
    }

    @Override
    public UploadImageResult upload(UploadImageCommand command) {
        if (!ALLOWED_CATEGORIES.contains(command.category())) {
            throw new UnsupportedCategoryException(command.category());
        }
        if (!ALLOWED_CONTENT_TYPES.contains(command.contentType())) {
            throw new UnsupportedFileTypeException(command.contentType());
        }
        if (command.content().length > MAX_BYTES) {
            throw new FileTooLargeException(MAX_BYTES);
        }

        String prefix = command.ownerId() != null ? command.category() + "/" + command.ownerId() : command.category();
        var stored = fileStoragePort.store(command.content(), command.originalFilename(), prefix);
        if (LIFECYCLE_TRACKED_CATEGORIES.contains(command.category())) {
            mediaAssetRepository.save(MediaAsset.uploaded(stored.objectKey(), stored.url()));
        }
        return new UploadImageResult(stored.url());
    }
}
