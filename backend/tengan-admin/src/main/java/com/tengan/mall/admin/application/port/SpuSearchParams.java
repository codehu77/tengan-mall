package com.tengan.mall.admin.application.port;

public record SpuSearchParams(Long categoryId, Long brandId, String name, Integer status, int pageNum,
        int pageSize) {
}
