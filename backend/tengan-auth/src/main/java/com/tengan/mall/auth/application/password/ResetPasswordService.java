package com.tengan.mall.auth.application.password;

import com.tengan.mall.auth.application.port.AccessTokenIssuerPort;
import com.tengan.mall.auth.application.port.RefreshTokenStorePort;
import com.tengan.mall.auth.application.port.VerificationTokenStorePort;
import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.exception.InvalidOrExpiredVerificationTokenException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消費 resetToken、改密碼、撤銷該帳號所有現存 refresh token（強制其他裝置登出）、
 * auto-login（視同 rememberMe=true，跟註冊完成同一套語意）。
 */
@Service
public class ResetPasswordService implements ResetPasswordUseCase {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenStorePort verificationTokenStorePort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final AccessTokenIssuerPort accessTokenIssuerPort;

    public ResetPasswordService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            VerificationTokenStorePort verificationTokenStorePort, RefreshTokenStorePort refreshTokenStorePort,
            AccessTokenIssuerPort accessTokenIssuerPort) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenStorePort = verificationTokenStorePort;
        this.refreshTokenStorePort = refreshTokenStorePort;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
    }

    @Override
    @Transactional
    public ResetPasswordResult reset(ResetPasswordCommand command) {
        Map<String, String> payload = verificationTokenStorePort.consume(command.resetToken())
                .orElseThrow(InvalidOrExpiredVerificationTokenException::new);
        Long accountId = Long.valueOf(payload.get("accountId"));

        Account account = accountRepository.findById(new AccountId(accountId))
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.changePassword(passwordEncoder.encode(command.newPassword()));
        accountRepository.save(account);

        refreshTokenStorePort.revokeAllForAccount(accountId);

        String accessToken = accessTokenIssuerPort.issue(account.getId());
        String refreshToken = refreshTokenStorePort.issue(accountId, true);
        long ttlSeconds = refreshTokenStorePort.ttlSecondsFor(true);

        return new ResetPasswordResult(accountId, accessToken, refreshToken, ttlSeconds);
    }
}
