package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.oauth.LinkFacebookAccountCommand;
import com.tengan.mall.auth.application.oauth.LinkFacebookAccountUseCase;
import com.tengan.mall.auth.application.oauth.LinkGoogleAccountCommand;
import com.tengan.mall.auth.application.oauth.LinkGoogleAccountUseCase;
import com.tengan.mall.auth.application.oauth.LinkLineAccountCommand;
import com.tengan.mall.auth.application.oauth.LinkLineAccountUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.LinkFacebookAccountRequest;
import com.tengan.mall.auth.interfaces.rest.dto.LinkGoogleAccountRequest;
import com.tengan.mall.auth.interfaces.rest.dto.LinkLineAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 沿用既有 /api/customer/** matcher（SecurityConfig 的 customerChain，強制持有效 access token）。 */
@RestController
@RequestMapping("/api/customer/auth/oauth2")
public class CustomerOAuthController {

    private final LinkGoogleAccountUseCase linkGoogleAccountUseCase;
    private final LinkFacebookAccountUseCase linkFacebookAccountUseCase;
    private final LinkLineAccountUseCase linkLineAccountUseCase;

    public CustomerOAuthController(LinkGoogleAccountUseCase linkGoogleAccountUseCase,
            LinkFacebookAccountUseCase linkFacebookAccountUseCase, LinkLineAccountUseCase linkLineAccountUseCase) {
        this.linkGoogleAccountUseCase = linkGoogleAccountUseCase;
        this.linkFacebookAccountUseCase = linkFacebookAccountUseCase;
        this.linkLineAccountUseCase = linkLineAccountUseCase;
    }

    @PostMapping("/google/link")
    public ResponseEntity<Void> linkGoogle(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody LinkGoogleAccountRequest request) {
        Long accountId = Long.valueOf(userJwt.getSubject());
        linkGoogleAccountUseCase.link(new LinkGoogleAccountCommand(accountId, request.idToken()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/facebook/link")
    public ResponseEntity<Void> linkFacebook(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody LinkFacebookAccountRequest request) {
        Long accountId = Long.valueOf(userJwt.getSubject());
        linkFacebookAccountUseCase.link(new LinkFacebookAccountCommand(accountId, request.accessToken()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/line/link")
    public ResponseEntity<Void> linkLine(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody LinkLineAccountRequest request) {
        Long accountId = Long.valueOf(userJwt.getSubject());
        linkLineAccountUseCase.link(new LinkLineAccountCommand(accountId, request.code(), request.nonce()));
        return ResponseEntity.noContent().build();
    }
}
