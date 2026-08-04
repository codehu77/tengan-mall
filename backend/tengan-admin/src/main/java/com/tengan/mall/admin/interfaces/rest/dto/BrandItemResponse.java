package com.tengan.mall.admin.interfaces.rest.dto;

public record BrandItemResponse(Long id, String name, String logo, String descript, String firstLetter, int sort,
        int status) {
}
