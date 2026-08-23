package com.tengan.mall.media.interfaces.rest.dto;

public record BannerResponse(Long id, String imageUrl, String linkUrl, String title, int sortOrder,
        boolean enabled) {
}
