package com.tengan.mall.devtools.client.dto;

/** 對應 tengan-admin AdminAuthController 的 LoginResponse，只取轉發 X-Identity-Assertion 用得到的 accessToken。 */
public record AdminLoginResponse(String accessToken) {
}
