package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record MenuMetaResponse(String title, String icon, int rank, List<String> auths) {
}
