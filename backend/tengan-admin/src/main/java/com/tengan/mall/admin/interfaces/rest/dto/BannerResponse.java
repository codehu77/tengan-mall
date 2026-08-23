package com.tengan.mall.admin.interfaces.rest.dto;

public record BannerResponse(Long id, String imageUrl, String linkUrl, String title, int sortOrder,
        boolean enabled) {
}
