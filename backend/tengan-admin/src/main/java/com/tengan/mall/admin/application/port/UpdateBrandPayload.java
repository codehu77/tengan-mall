package com.tengan.mall.admin.application.port;

public record UpdateBrandPayload(String name, String logo, String descript, String firstLetter, int sort) {
}
