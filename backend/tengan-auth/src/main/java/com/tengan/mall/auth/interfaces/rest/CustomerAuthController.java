package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.logout.LogoutCommand;
import com.tengan.mall.auth.application.logout.LogoutUseCase;
import com.tengan.mall.auth.application.me.GetMeQuery;
import com.tengan.mall.auth.application.me.GetMeUseCase;
import com.tengan.mall.auth.application.refresh.RefreshCommand;
import com.tengan.mall.auth.application.refresh.RefreshUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.LogoutRequest;
import com.tengan.mall.auth.interfaces.rest.dto.MeResponse;
import com.tengan.mall.auth.interfaces.rest.dto.RefreshRequest;
import com.tengan.mall.auth.interfaces.rest.dto.RefreshResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/auth")
public class CustomerAuthController {

    private final LogoutUseCase logoutUseCase;
    private final RefreshUseCase refreshUseCase;
    private final GetMeUseCase getMeUseCase;

    public CustomerAuthController(LogoutUseCase logoutUseCase, RefreshUseCase refreshUseCase,
            GetMeUseCase getMeUseCase) {
        this.logoutUseCase = logoutUseCase;
        this.refreshUseCase = refreshUseCase;
        this.getMeUseCase = getMeUseCase;
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        logoutUseCase.logout(new LogoutCommand(request.refreshToken()));
    }

    /** 這支不驗 access token（見 SecurityConfig 的 refreshChain 註解），驗的是 request body 這顆 refresh token。 */
    @PostMapping("/refresh")
    public RefreshResponse refresh(@Valid @RequestBody RefreshRequest request) {
        var result = refreshUseCase.refresh(new RefreshCommand(request.refreshToken()));
        return new RefreshResponse(result.accessToken(), result.refreshToken());
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt userJwt) {
        Long accountId = Long.valueOf(userJwt.getSubject());
        var result = getMeUseCase.getMe(new GetMeQuery(accountId));
        return new MeResponse(result.accountId(), result.username(), result.phone());
    }
}
