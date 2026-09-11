package com.tengan.mall.media.infrastructure.storage;

import com.tengan.mall.media.application.port.FileStoragePort;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MinioFileStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioFileStorageAdapter(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public StoredObject store(byte[] content, String originalFilename, String category) {
        String ext = StringUtils.getFilenameExtension(originalFilename);
        String objectKey = category + "/" + UUID.randomUUID() + (StringUtils.hasText(ext) ? "." + ext : "");
        String contentType = contentTypeOf(ext);
        try (var input = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .stream(input, content.length, -1)
                    .contentType(contentType)
                    .build());
        } catch (IOException e) {
            throw new UncheckedIOException("寫入 MinIO 失敗: " + objectKey, e);
        } catch (Exception e) {
            throw new IllegalStateException("寫入 MinIO 失敗: " + objectKey, e);
        }
        String url = properties.getEndpoint() + "/" + properties.getBucket() + "/" + objectKey;
        return new StoredObject(objectKey, url);
    }

    @Override
    public void deleteIfOwned(String url) {
        String prefix = properties.getEndpoint() + "/" + properties.getBucket() + "/";
        if (url == null || !url.startsWith(prefix)) {
            return;
        }
        String objectKey = url.substring(prefix.length());
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(properties.getBucket()).object(objectKey)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("刪除 MinIO 物件失敗: " + objectKey, e);
        }
    }

    private String contentTypeOf(String ext) {
        if (ext == null) {
            return "application/octet-stream";
        }
        return switch (ext.toLowerCase()) {
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> "image/jpeg";
        };
    }
}
