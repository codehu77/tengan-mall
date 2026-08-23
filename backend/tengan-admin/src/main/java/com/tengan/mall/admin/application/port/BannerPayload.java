package com.tengan.mall.admin.application.port;

public record BannerPayload(String imageUrl, String linkUrl, String title, int sortOrder, boolean enabled) {
}
