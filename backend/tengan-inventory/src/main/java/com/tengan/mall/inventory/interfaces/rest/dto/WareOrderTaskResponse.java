package com.tengan.mall.inventory.interfaces.rest.dto;

public record WareOrderTaskResponse(Long id, String orderSn, int status, String createdAt) {
}
