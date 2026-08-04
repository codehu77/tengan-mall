package com.tengan.mall.admin.application.port;

public record CreateBrandPayload(String name, String logo, String descript, String firstLetter, int sort) {
}
