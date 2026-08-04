package com.tengan.mall.auth.interfaces.rest.dto;

public record LoginResponse(String accessToken, String refreshToken, Long accountId, String username) {
}
