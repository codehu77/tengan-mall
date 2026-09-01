package com.tengan.mall.auth.interfaces.rest.dto;

public record MeResponse(Long accountId, String phone, String email, boolean googleLinked) {
}
