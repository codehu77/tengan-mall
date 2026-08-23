package com.tengan.mall.media.application.upload;

import com.tengan.mall.media.application.port.FileStoragePort;
import com.tengan.mall.media.domain.exception.FileTooLargeException;
import com.tengan.mall.media.domain.exception.UnsupportedCategoryException;
import com.tengan.mall.media.domain.exception.UnsupportedFileTypeException;
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

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("avatar", "banner", "product", "admin-avatar");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_BYTES = 2L * 1024 * 1024;

    private final FileStoragePort fileStoragePort;

    public UploadImageService(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
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
        String url = fileStoragePort.store(command.content(), command.originalFilename(), prefix);
        return new UploadImageResult(url);
    }
}
