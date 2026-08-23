package com.tengan.mall.admin.application.port;

public interface FileStoragePort {

    /** 回傳可公開存取（不需帶 Authorization header）的完整圖片 URL。ownerId 是上傳者自己的 id，
     * 讓實作把同一 category 底下的檔案依擁有者分開存，不用扁平塞在同一個資料夾。 */
    String store(byte[] content, String originalFilename, String contentType, String category, Long ownerId);
}
