package com.tengan.mall.admin.application.upload;

import com.tengan.mall.admin.application.port.FileStoragePort;
import com.tengan.mall.admin.domain.exception.FileTooLargeException;
import com.tengan.mall.admin.domain.exception.UnsupportedFileTypeException;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class UploadFileService implements UploadFileUseCase {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_BYTES = 2L * 1024 * 1024;

    private final FileStoragePort fileStoragePort;

    public UploadFileService(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    public UploadFileResult upload(UploadFileCommand command) {
        if (!ALLOWED_CONTENT_TYPES.contains(command.contentType())) {
            throw new UnsupportedFileTypeException(command.contentType());
        }
        if (command.content().length > MAX_BYTES) {
            throw new FileTooLargeException(MAX_BYTES);
        }

        String url = fileStoragePort.store(command.content(), command.originalFilename(), command.category());
        return new UploadFileResult(url);
    }
}
