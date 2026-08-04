package com.tengan.mall.admin.interfaces.rest;

import com.nimbusds.jose.jwk.JWKSet;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公開簽章驗證金鑰集合，Gateway 的 adminJwtDecoder 指向這支端點（跟 tengan-auth 的
 * JwksController 對稱，見 docs/JWT設計.md）。{@code toJSONObject(true)} 一定要傳 true——
 * 只輸出公開金鑰欄位，不能把 AdminJwtKeyConfig 裡簽章用的私鑰洩漏出去。
 */
@RestController
public class AdminJwksController {

    private final JWKSet jwkSet;

    public AdminJwksController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping("/oauth2/jwks")
    public Map<String, Object> keys() {
        return jwkSet.toJSONObject(true);
    }
}
