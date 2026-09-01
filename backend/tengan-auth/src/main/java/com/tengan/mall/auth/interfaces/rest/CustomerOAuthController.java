package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.oauth.LinkGoogleAccountCommand;
import com.tengan.mall.auth.application.oauth.LinkGoogleAccountUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.LinkGoogleAccountRequest;
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

    public CustomerOAuthController(LinkGoogleAccountUseCase linkGoogleAccountUseCase) {
        this.linkGoogleAccountUseCase = linkGoogleAccountUseCase;
    }

    @PostMapping("/google/link")
    public ResponseEntity<Void> linkGoogle(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody LinkGoogleAccountRequest request) {
        Long accountId = Long.valueOf(userJwt.getSubject());
        linkGoogleAccountUseCase.link(new LinkGoogleAccountCommand(accountId, request.idToken()));
        return ResponseEntity.noContent().build();
    }
}
