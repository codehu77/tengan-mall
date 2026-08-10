package com.tengan.mall.order.application.order;

public record ListMyOrdersQuery(Long memberId, Integer status, int pageNum, int pageSize) {
}
