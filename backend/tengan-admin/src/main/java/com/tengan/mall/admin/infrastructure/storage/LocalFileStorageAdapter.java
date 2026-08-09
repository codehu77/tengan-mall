package com.tengan.mall.admin.infrastructure.storage;

import com.tengan.mall.admin.application.port.FileStoragePort;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 頭像量體小、後台單副本運行，本機磁碟 + Spring 靜態資源伺服（見 WebMvcConfig）就夠用，
 * 不需要 MinIO。之後如果真的要換，只要換一個 FileStoragePort 實作，不用動 Controller/Service。
 */
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path uploadDir;
    private final String publicBaseUrl;

    public LocalFileStorageAdapter(@Value("${app.upload.dir:./data/admin-uploads}") String uploadDir,
            @Value("${app.upload.public-base-url:/api/admin/static}") String publicBaseUrl) {
        this.uploadDir = Path.of(uploadDir);
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    public String store(byte[] content, String originalFilename, String category) {
        String ext = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + (StringUtils.hasText(ext) ? "." + ext : "");
        Path targetDir = uploadDir.resolve(category);
        Path targetFile = targetDir.resolve(filename);
        try {
            Files.createDirectories(targetDir);
            Files.write(targetFile, content);
        } catch (IOException e) {
            throw new UncheckedIOException("寫入上傳檔案失敗: " + targetFile, e);
        }
        return publicBaseUrl + "/" + category + "/" + filename;
    }
}
