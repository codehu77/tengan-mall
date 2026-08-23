package com.tengan.mall.media.application.banner;

/** 首頁公開端點用——不含 sortOrder/enabled 這些後台管理欄位。 */
public record PublicBannerView(Long id, String imageUrl, String linkUrl, String title) {
}
