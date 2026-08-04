package com.tengan.mall.product.application.spu;

public record SearchSpusQuery(Long categoryId, Long brandId, String name, Integer status, int pageNum,
        int pageSize) {
}
