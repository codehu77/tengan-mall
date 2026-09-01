package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.contact.ChangeEmailStartCommand;
import com.tengan.mall.auth.application.contact.ChangeEmailStartUseCase;
import com.tengan.mall.auth.application.contact.ChangeEmailVerifyCommand;
import com.tengan.mall.auth.application.contact.ChangeEmailVerifyUseCase;
import com.tengan.mall.auth.application.contact.ChangePhoneStartCommand;
import com.tengan.mall.auth.application.contact.ChangePhoneStartUseCase;
import com.tengan.mall.auth.application.contact.ChangePhoneVerifyCommand;
import com.tengan.mall.auth.application.contact.ChangePhoneVerifyUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.ChangeEmailStartRequest;
import com.tengan.mall.auth.interfaces.rest.dto.ChangeEmailStartResponse;
import com.tengan.mall.auth.interfaces.rest.dto.ChangeEmailVerifyRequest;
import com.tengan.mall.auth.interfaces.rest.dto.ChangePhoneStartRequest;
import com.tengan.mall.auth.interfaces.rest.dto.ChangePhoneStartResponse;
import com.tengan.mall.auth.interfaces.rest.dto.ChangePhoneVerifyRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 掛在 /api/customer/auth/contact/**——不是 /api/customer/account/**，因為 tengan-gateway 的
 * 路由規則是精確前綴比對（Path=/api/customer/auth/**），不是泛用的 /api/customer/**，
 * 掛在別的前綴下 Gateway 會直接 404、根本轉發不到這裡（實測踩過這個坑）。
 */
@RestController
@RequestMapping("/api/customer/auth/contact")
public class CustomerContactController {

    private final ChangePhoneStartUseCase changePhoneStartUseCase;
    private final ChangePhoneVerifyUseCase changePhoneVerifyUseCase;
    private final ChangeEmailStartUseCase changeEmailStartUseCase;
    private final ChangeEmailVerifyUseCase changeEmailVerifyUseCase;

    public CustomerContactController(ChangePhoneStartUseCase changePhoneStartUseCase,
            ChangePhoneVerifyUseCase changePhoneVerifyUseCase, ChangeEmailStartUseCase changeEmailStartUseCase,
            ChangeEmailVerifyUseCase changeEmailVerifyUseCase) {
        this.changePhoneStartUseCase = changePhoneStartUseCase;
        this.changePhoneVerifyUseCase = changePhoneVerifyUseCase;
        this.changeEmailStartUseCase = changeEmailStartUseCase;
        this.changeEmailVerifyUseCase = changeEmailVerifyUseCase;
    }

    @PostMapping("/phone/start")
    public ChangePhoneStartResponse phoneStart(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody ChangePhoneStartRequest request) {
        var result = changePhoneStartUseCase.start(
                new ChangePhoneStartCommand(accountId(userJwt), request.newPhone(), request.currentPassword()));
        return new ChangePhoneStartResponse(result.code());
    }

    @PostMapping("/phone/verify")
    public ResponseEntity<Void> phoneVerify(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody ChangePhoneVerifyRequest request) {
        changePhoneVerifyUseCase.verify(
                new ChangePhoneVerifyCommand(accountId(userJwt), request.newPhone(), request.code()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/start")
    public ChangeEmailStartResponse emailStart(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody ChangeEmailStartRequest request) {
        var result = changeEmailStartUseCase.start(
                new ChangeEmailStartCommand(accountId(userJwt), request.newEmail(), request.currentPassword()));
        return new ChangeEmailStartResponse(result.code());
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Void> emailVerify(@AuthenticationPrincipal Jwt userJwt,
            @Valid @RequestBody ChangeEmailVerifyRequest request) {
        changeEmailVerifyUseCase.verify(
                new ChangeEmailVerifyCommand(accountId(userJwt), request.newEmail(), request.code()));
        return ResponseEntity.noContent().build();
    }

    private Long accountId(Jwt userJwt) {
        return Long.valueOf(userJwt.getSubject());
    }
}
