package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.login.LoginCommand;
import com.tengan.mall.auth.application.login.LoginUseCase;
import com.tengan.mall.auth.application.register.RegisterCommand;
import com.tengan.mall.auth.application.register.RegisterUseCase;
import com.tengan.mall.auth.application.sms.SendSmsCodeCommand;
import com.tengan.mall.auth.application.sms.SendSmsCodeUseCase;
import com.tengan.mall.auth.application.sms.VerifySmsCodeCommand;
import com.tengan.mall.auth.application.sms.VerifySmsCodeUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.LoginRequest;
import com.tengan.mall.auth.interfaces.rest.dto.LoginResponse;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterRequest;
import com.tengan.mall.auth.interfaces.rest.dto.RegisterResponse;
import com.tengan.mall.auth.interfaces.rest.dto.SendSmsCodeRequest;
import com.tengan.mall.auth.interfaces.rest.dto.SendSmsCodeResponse;
import com.tengan.mall.auth.interfaces.rest.dto.VerifySmsCodeRequest;
import com.tengan.mall.auth.interfaces.rest.dto.VerifySmsCodeResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
public class PublicAuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final SendSmsCodeUseCase sendSmsCodeUseCase;
    private final VerifySmsCodeUseCase verifySmsCodeUseCase;

    public PublicAuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase,
            SendSmsCodeUseCase sendSmsCodeUseCase, VerifySmsCodeUseCase verifySmsCodeUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.sendSmsCodeUseCase = sendSmsCodeUseCase;
        this.verifySmsCodeUseCase = verifySmsCodeUseCase;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        var result = registerUseCase.register(
                new RegisterCommand(request.username(), request.phone(), request.password(), request.code()));
        return new RegisterResponse(result.accountId(), result.username());
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var result = loginUseCase.login(new LoginCommand(request.username(), request.password()));
        return new LoginResponse(result.accessToken(), result.refreshToken(), result.accountId(), result.username());
    }

    @PostMapping("/sms/send")
    public SendSmsCodeResponse sendSmsCode(@Valid @RequestBody SendSmsCodeRequest request) {
        String code = sendSmsCodeUseCase.sendCode(new SendSmsCodeCommand(request.phone(), request.purpose()));
        return new SendSmsCodeResponse(code);
    }

    @PostMapping("/sms/verify")
    public VerifySmsCodeResponse verifySmsCode(@Valid @RequestBody VerifySmsCodeRequest request) {
        boolean valid = verifySmsCodeUseCase.verify(
                new VerifySmsCodeCommand(request.phone(), request.purpose(), request.code()));
        return new VerifySmsCodeResponse(valid);
    }
}
