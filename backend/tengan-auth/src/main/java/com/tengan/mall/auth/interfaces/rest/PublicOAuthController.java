package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.oauth.FacebookLoginCommand;
import com.tengan.mall.auth.application.oauth.FacebookLoginUseCase;
import com.tengan.mall.auth.application.oauth.GoogleLoginCommand;
import com.tengan.mall.auth.application.oauth.GoogleLoginUseCase;
import com.tengan.mall.auth.application.oauth.LineLoginCommand;
import com.tengan.mall.auth.application.oauth.LineLoginUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.FacebookLoginRequest;
import com.tengan.mall.auth.interfaces.rest.dto.FacebookLoginResponse;
import com.tengan.mall.auth.interfaces.rest.dto.GoogleLoginRequest;
import com.tengan.mall.auth.interfaces.rest.dto.GoogleLoginResponse;
import com.tengan.mall.auth.interfaces.rest.dto.LineLoginRequest;
import com.tengan.mall.auth.interfaces.rest.dto.LineLoginResponse;
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
    private final FacebookLoginUseCase facebookLoginUseCase;
    private final LineLoginUseCase lineLoginUseCase;

    public PublicOAuthController(GoogleLoginUseCase googleLoginUseCase, FacebookLoginUseCase facebookLoginUseCase,
            LineLoginUseCase lineLoginUseCase) {
        this.googleLoginUseCase = googleLoginUseCase;
        this.facebookLoginUseCase = facebookLoginUseCase;
        this.lineLoginUseCase = lineLoginUseCase;
    }

    @PostMapping("/google")
    public GoogleLoginResponse google(@Valid @RequestBody GoogleLoginRequest request) {
        var result = googleLoginUseCase.login(new GoogleLoginCommand(request.idToken()));
        return new GoogleLoginResponse(result.accountId(), result.accessToken(), result.refreshToken(),
                result.refreshTokenTtlSeconds());
    }

    @PostMapping("/facebook")
    public FacebookLoginResponse facebook(@Valid @RequestBody FacebookLoginRequest request) {
        var result = facebookLoginUseCase.login(new FacebookLoginCommand(request.accessToken()));
        return new FacebookLoginResponse(result.accountId(), result.accessToken(), result.refreshToken(),
                result.refreshTokenTtlSeconds());
    }

    @PostMapping("/line")
    public LineLoginResponse line(@Valid @RequestBody LineLoginRequest request) {
        var result = lineLoginUseCase.login(new LineLoginCommand(request.code(), request.nonce()));
        return new LineLoginResponse(result.accountId(), result.accessToken(), result.refreshToken(),
                result.refreshTokenTtlSeconds());
    }
}
