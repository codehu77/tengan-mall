package com.tengan.mall.admin.application.port;

/**
 * 轉發圖片上傳到 tengan-media，跟顧客頭像上傳的 category 分開命名空間。ownerId 選填——
 * 管理員自己的頭像（category=admin-avatar）會帶自己的 adminId，讓 tengan-media 依擁有者分
 * 資料夾；Banner/商品圖（category=banner/product）沒有擁有者概念，傳 null。
 */
public interface MediaUploadPort {

    String uploadImage(byte[] content, String originalFilename, String contentType, String category, Long ownerId);
}
