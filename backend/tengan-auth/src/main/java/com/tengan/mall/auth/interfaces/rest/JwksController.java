package com.tengan.mall.auth.interfaces.rest;

import com.nimbusds.jose.jwk.JWKSet;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公開簽章驗證金鑰集合，Gateway 與其他服務的 userJwtDecoder 都指向這支端點
 * （docs/JWT設計.md 第一節）。{@code toJSONObject(true)} 一定要傳 true——只輸出
 * 公開金鑰欄位，不能把 {@code JwtKeyConfig} 裡簽章用的私鑰洩漏出去。
 */
@RestController
public class JwksController {

    private final JWKSet jwkSet;

    public JwksController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping("/oauth2/jwks")
    public Map<String, Object> keys() {
        return jwkSet.toJSONObject(true);
    }
}
