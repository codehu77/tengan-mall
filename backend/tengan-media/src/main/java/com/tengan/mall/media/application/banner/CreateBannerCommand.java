package com.tengan.mall.media.application.banner;

public record CreateBannerCommand(String imageUrl, String linkUrl, String title, int sortOrder, boolean enabled) {
}
