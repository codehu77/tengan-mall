package com.tengan.mall.auth.application.login;

import com.tengan.mall.auth.application.port.AccessTokenIssuerPort;
import com.tengan.mall.auth.application.port.RefreshTokenStorePort;
import com.tengan.mall.auth.domain.exception.InvalidCredentialsException;
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

    public LoginService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            AccessTokenIssuerPort accessTokenIssuerPort, RefreshTokenStorePort refreshTokenStorePort) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
    }

    @Override
    public LoginResult login(LoginCommand command) {
        Account account = accountRepository.findByUsername(command.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (!account.isActive() || !passwordEncoder.matches(command.password(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String username = account.getUsername().value();
        String accessToken = accessTokenIssuerPort.issue(account.getId(), username);
        String refreshToken = refreshTokenStorePort.issue(account.getId().value());

        return new LoginResult(accessToken, refreshToken, account.getId().value(), username);
    }
}
