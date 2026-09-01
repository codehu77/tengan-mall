package com.tengan.mall.auth.application.login;

import com.tengan.mall.auth.application.port.AccessTokenIssuerPort;
import com.tengan.mall.auth.application.port.LoginAttemptLimiterPort;
import com.tengan.mall.auth.application.port.RefreshTokenStorePort;
import com.tengan.mall.auth.domain.exception.InvalidCredentialsException;
import com.tengan.mall.auth.domain.exception.TooManyLoginAttemptsException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenIssuerPort accessTokenIssuerPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final LoginAttemptLimiterPort loginAttemptLimiterPort;

    public LoginService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            AccessTokenIssuerPort accessTokenIssuerPort, RefreshTokenStorePort refreshTokenStorePort,
            LoginAttemptLimiterPort loginAttemptLimiterPort) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
        this.loginAttemptLimiterPort = loginAttemptLimiterPort;
    }

    @Override
    public LoginResult login(LoginCommand command) {
        String identifier = command.identifier();
        if (loginAttemptLimiterPort.isLocked(identifier)) {
            throw new TooManyLoginAttemptsException();
        }

        Account account = accountRepository.findByIdentifier(identifier).orElse(null);
        // passwordEncoder.matches(raw, null) 安全回傳 false（BCryptPasswordEncoder 內部處理，
        // 不丟例外），OAuth-only 帳號（passwordHash 為 null）走到這裡自然視同密碼不符。
        boolean passwordMatches = account != null
                && passwordEncoder.matches(command.password(), account.getPasswordHash());
        if (account == null || !account.isActive() || !passwordMatches) {
            loginAttemptLimiterPort.recordFailure(identifier);
            throw new InvalidCredentialsException();
        }
        loginAttemptLimiterPort.reset(identifier);

        String accessToken = accessTokenIssuerPort.issue(account.getId());
        String refreshToken = refreshTokenStorePort.issue(account.getId().value(), command.rememberMe());
        long ttlSeconds = refreshTokenStorePort.ttlSecondsFor(command.rememberMe());

        return new LoginResult(accessToken, refreshToken, account.getId().value(), ttlSeconds);
    }
}
