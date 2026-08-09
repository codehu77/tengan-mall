package com.tengan.mall.admin.application.upload;

public record UploadFileCommand(byte[] content, String originalFilename, String contentType, String category) {
}
