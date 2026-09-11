package com.tengan.mall.media.application.port;

public interface FileStoragePort {

    /** objectKey 用 UUID 命名（見實作），刻意跟資料庫自增 id 脫鉤——bucket 是匿名公開讀，
     * key 本身是唯一保護，不能用可枚舉的 id 當物件命名。url 是可公開存取（不需帶 Authorization
     * header）的完整圖片網址。 */
    StoredObject store(byte[] content, String originalFilename, String category);

    /**
     * url 不是這個物件儲存自己的網域/bucket 時安靜略過（例如後台手動貼的外部圖片網址，
     * 不是我們自己上傳的檔案，沒有對應物件可刪）。
     */
    void deleteIfOwned(String url);

    record StoredObject(String objectKey, String url) {
    }
}
