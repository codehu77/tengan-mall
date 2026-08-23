package com.tengan.mall.media.application.banner;

public record BannerView(Long id, String imageUrl, String linkUrl, String title, int sortOrder, boolean enabled) {
}
