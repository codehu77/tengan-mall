package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.login.LoginCommand;
import com.tengan.mall.auth.application.login.LoginUseCase;
import com.tengan.mall.auth.application.register.RegisterCompleteCommand;
import com.tengan.mall.auth.application.register.RegisterCompleteUseCase;
import com.tengan.mall.auth.application.register.RegisterStartCommand;
import com.tengan.mall.auth.application.register.RegisterStartUseCase;
import com.tengan.mall.auth.application.register.RegisterVerifyCommand;
import com.tengan.mall.auth.application.register.RegisterVerifyUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.LoginRequest;
import com.tengan.mall.auth.interfaces.rest.dto.LoginResponse;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterCompleteRequest;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterCompleteResponse;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterStartRequest;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterStartResponse;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterVerifyRequest;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterVerifyResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
public class PublicAuthController {

    private final RegisterStartUseCase registerStartUseCase;
    private final RegisterVerifyUseCase registerVerifyUseCase;
    private final RegisterCompleteUseCase registerCompleteUseCase;
    private final LoginUseCase loginUseCase;

    public PublicAuthController(RegisterStartUseCase registerStartUseCase,
            RegisterVerifyUseCase registerVerifyUseCase, RegisterCompleteUseCase registerCompleteUseCase,
            LoginUseCase loginUseCase) {
        this.registerStartUseCase = registerStartUseCase;
        this.registerVerifyUseCase = registerVerifyUseCase;
        this.registerCompleteUseCase = registerCompleteUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register/start")
    public RegisterStartResponse registerStart(@Valid @RequestBody RegisterStartRequest request) {
        var result = registerStartUseCase.start(new RegisterStartCommand(request.identifier()));
        return new RegisterStartResponse(result.code());
    }

    @PostMapping("/register/verify")
    public RegisterVerifyResponse registerVerify(@Valid @RequestBody RegisterVerifyRequest request) {
        var result = registerVerifyUseCase.verify(new RegisterVerifyCommand(request.identifier(), request.code()));
        return new RegisterVerifyResponse(result.registrationToken());
    }

    @PostMapping("/register/complete")
    public RegisterCompleteResponse registerComplete(@Valid @RequestBody RegisterCompleteRequest request) {
        var result = registerCompleteUseCase.complete(
                new RegisterCompleteCommand(request.registrationToken(), request.password()));
        return new RegisterCompleteResponse(result.accountId(), result.accessToken(), result.refreshToken(),
                result.refreshTokenTtlSeconds());
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var result = loginUseCase.login(
                new LoginCommand(request.identifier(), request.password(), request.rememberMe()));
        return new LoginResponse(result.accessToken(), result.refreshToken(), result.accountId(),
                result.refreshTokenTtlSeconds());
    }
}
