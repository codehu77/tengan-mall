package com.tengan.mall.admin.infrastructure.storage;

import com.tengan.mall.admin.application.port.FileStoragePort;
import com.tengan.mall.admin.application.port.MediaUploadPort;
import org.springframework.stereotype.Component;

/**
 * 取代原本的 LocalFileStorageAdapter（本機磁碟）——管理員頭像現在跟 Banner/商品圖走同一條物件
 * 儲存路徑，透過既有的 {@link MediaUploadPort}（服務對服務轉發 multipart 到 tengan-media）真的
 * 存進 MinIO，不再依賴本機磁碟+靜態資源伺服。這就是 LocalFileStorageAdapter 當初 javadoc 說的
 * 「之後如果真的要換，只要換一個 FileStoragePort 實作」——Controller/Service 完全沒改。
 */
@Component
public class MediaFileStorageAdapter implements FileStoragePort {

    private final MediaUploadPort mediaUploadPort;

    public MediaFileStorageAdapter(MediaUploadPort mediaUploadPort) {
        this.mediaUploadPort = mediaUploadPort;
    }

    @Override
    public String store(byte[] content, String originalFilename, String contentType, String category,
            Long ownerId) {
        return mediaUploadPort.uploadImage(content, originalFilename, contentType, category, ownerId);
    }
}
