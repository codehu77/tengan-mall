package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.password.ForgotPasswordCommand;
import com.tengan.mall.auth.application.password.ForgotPasswordUseCase;
import com.tengan.mall.auth.application.password.ForgotPasswordVerifyCommand;
import com.tengan.mall.auth.application.password.ForgotPasswordVerifyUseCase;
import com.tengan.mall.auth.application.password.ResetPasswordCommand;
import com.tengan.mall.auth.application.password.ResetPasswordUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.ForgotPasswordRequest;
import com.tengan.mall.auth.interfaces.rest.dto.ForgotPasswordResponse;
import com.tengan.mall.auth.interfaces.rest.dto.ForgotPasswordVerifyRequest;
import com.tengan.mall.auth.interfaces.rest.dto.ForgotPasswordVerifyResponse;
import com.tengan.mall.auth.interfaces.rest.dto.ResetPasswordRequest;
import com.tengan.mall.auth.interfaces.rest.dto.ResetPasswordResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 掛在 /api/public/auth/password/**，沿用 SecurityConfig 既有的 publicChain matcher，不用改。 */
@RestController
@RequestMapping("/api/public/auth/password")
public class PublicPasswordController {

    private static final String ENUMERATION_SAFE_MESSAGE = "若此帳號存在，驗證碼已寄出";

    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ForgotPasswordVerifyUseCase forgotPasswordVerifyUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    public PublicPasswordController(ForgotPasswordUseCase forgotPasswordUseCase,
            ForgotPasswordVerifyUseCase forgotPasswordVerifyUseCase, ResetPasswordUseCase resetPasswordUseCase) {
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.forgotPasswordVerifyUseCase = forgotPasswordVerifyUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    /** 不管 service 內部是否真的發了 OTP，永遠回同一句訊息，防止帳號列舉。 */
    @PostMapping("/forgot")
    public ForgotPasswordResponse forgot(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.forgot(new ForgotPasswordCommand(request.identifier()));
        return new ForgotPasswordResponse(ENUMERATION_SAFE_MESSAGE);
    }

    @PostMapping("/forgot/verify")
    public ForgotPasswordVerifyResponse forgotVerify(@Valid @RequestBody ForgotPasswordVerifyRequest request) {
        var result = forgotPasswordVerifyUseCase.verify(
                new ForgotPasswordVerifyCommand(request.identifier(), request.code()));
        return new ForgotPasswordVerifyResponse(result.resetToken());
    }

    @PostMapping("/reset")
    public ResetPasswordResponse reset(@Valid @RequestBody ResetPasswordRequest request) {
        var result = resetPasswordUseCase.reset(
                new ResetPasswordCommand(request.resetToken(), request.newPassword()));
        return new ResetPasswordResponse(result.accountId(), result.accessToken(), result.refreshToken(),
                result.refreshTokenTtlSeconds());
    }
}
