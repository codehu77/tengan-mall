package com.tengan.mall.media.application.banner;

public record UpdateBannerCommand(Long id, String imageUrl, String linkUrl, String title, int sortOrder,
        boolean enabled) {
}
