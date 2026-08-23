package com.tengan.mall.media.application.upload;

import com.tengan.mall.media.application.port.FileStoragePort;
import org.springframework.stereotype.Service;

/** best-effort 批次刪除——呼叫端（例如 tengan-product 刪除 SPU 時）一次把所有相關圖片網址丟過來，
 * 不是我們自己 bucket 的網址（後台手動貼的外部圖片網址）由 {@link FileStoragePort#deleteIfOwned} 安靜略過。 */
@Service
public class DeleteObjectsService implements DeleteObjectsUseCase {

    private final FileStoragePort fileStoragePort;

    public DeleteObjectsService(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    public void delete(DeleteObjectsCommand command) {
        for (String url : command.urls()) {
            if (url == null || url.isBlank()) {
                continue;
            }
            fileStoragePort.deleteIfOwned(url);
        }
    }
}
