package com.tengan.mall.admin.application.port;

public interface FileStoragePort {

    /** 回傳可公開存取（不需帶 Authorization header）的完整圖片 URL。 */
    String store(byte[] content, String originalFilename, String category);
}
