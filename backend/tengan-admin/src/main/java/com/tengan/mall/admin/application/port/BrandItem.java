package com.tengan.mall.admin.application.port;

public record BrandItem(Long id, String name, String logo, String descript, String firstLetter, int sort,
        int status) {
}
