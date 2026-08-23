package com.tengan.mall.admin.application.port;

public record BannerItem(Long id, String imageUrl, String linkUrl, String title, int sortOrder, boolean enabled) {
}
