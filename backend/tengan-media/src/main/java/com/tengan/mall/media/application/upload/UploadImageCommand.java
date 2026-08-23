package com.tengan.mall.media.application.upload;

/**
 * ownerId 只有 category="avatar" 時才有值——每個會員的頭像各自存進 avatar/{memberId}/ 底下，
 * 而不是全部會員的頭像扁平塞進同一個 avatar/ 資料夾。banner/product 沒有「擁有者」這個概念
 * （banner 本身就是後台的展示內容、product 圖片的歸屬已經記在 tengan-product 自己的表），
 * ownerId 傳 null 即可。
 */
public record UploadImageCommand(byte[] content, String originalFilename, String contentType, String category,
        Long ownerId) {
}
