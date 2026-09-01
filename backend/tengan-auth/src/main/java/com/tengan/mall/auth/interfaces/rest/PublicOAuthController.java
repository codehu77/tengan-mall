package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.oauth.GoogleLoginCommand;
import com.tengan.mall.auth.application.oauth.GoogleLoginUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.GoogleLoginRequest;
import com.tengan.mall.auth.interfaces.rest.dto.GoogleLoginResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 沿用既有 /api/public/** matcher（SecurityConfig 的 publicChain），不用改。 */
@RestController
@RequestMapping("/api/public/auth/oauth2")
public class PublicOAuthController {

    private final GoogleLoginUseCase googleLoginUseCase;

    public PublicOAuthController(GoogleLoginUseCase googleLoginUseCase) {
        this.googleLoginUseCase = googleLoginUseCase;
    }

    @PostMapping("/google")
    public GoogleLoginResponse google(@Valid @RequestBody GoogleLoginRequest request) {
        var result = googleLoginUseCase.login(new GoogleLoginCommand(request.idToken()));
        return new GoogleLoginResponse(result.accountId(), result.accessToken(), result.refreshToken(),
                result.refreshTokenTtlSeconds());
    }
}
