package com.tengan.mall.media.domain.exception;

/** category 會直接組進物件儲存的 key 前綴，白名單擋掉，避免任意路徑穿越到別的資料夾。 */
public class UnsupportedCategoryException extends RuntimeException {

    public UnsupportedCategoryException(String category) {
        super("不支援的圖片分類: " + category);
    }
}
