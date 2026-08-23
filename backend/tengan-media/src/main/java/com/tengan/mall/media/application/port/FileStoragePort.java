package com.tengan.mall.media.application.port;

public interface FileStoragePort {

    /** 回傳可公開存取（不需帶 Authorization header）的完整圖片 URL。 */
    String store(byte[] content, String originalFilename, String category);

    /**
     * url 不是這個物件儲存自己的網域/bucket 時安靜略過（例如後台手動貼的外部圖片網址，
     * 不是我們自己上傳的檔案，沒有對應物件可刪）。
     */
    void deleteIfOwned(String url);
}
