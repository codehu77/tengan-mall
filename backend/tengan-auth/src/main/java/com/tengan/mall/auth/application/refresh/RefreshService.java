package com.tengan.mall.auth.application.refresh;

import com.tengan.mall.auth.application.port.AccessTokenIssuerPort;
import com.tengan.mall.auth.application.port.RefreshTokenEntry;
import com.tengan.mall.auth.application.port.RefreshTokenStorePort;
import com.tengan.mall.auth.domain.exception.InvalidRefreshTokenException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

/**
 * Rotation + reuse detection（微服務前台API待開發清單.md 第2節）：已標記 used=true 的
 * refresh token 又被使用，代表它已被複製，撤銷整個 family 強制重新登入，不是單純拒絕了事。
 */
@Service
public class RefreshService implements RefreshUseCase {

    private final AccountRepository accountRepository;
    private final AccessTokenIssuerPort accessTokenIssuerPort;
    private final RefreshTokenStorePort refreshTokenStorePort;

    public RefreshService(AccountRepository accountRepository, AccessTokenIssuerPort accessTokenIssuerPort,
            RefreshTokenStorePort refreshTokenStorePort) {
        this.accountRepository = accountRepository;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
    }

    @Override
    public RefreshResult refresh(RefreshCommand command) {
        RefreshTokenEntry entry = refreshTokenStorePort.find(command.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("not found or expired"));

        if (entry.used()) {
            refreshTokenStorePort.revokeFamily(entry.familyId());
            throw new InvalidRefreshTokenException("reuse detected, family revoked");
        }

        String newRefreshToken = refreshTokenStorePort.rotate(command.refreshToken(), entry);

        Account account = accountRepository.findById(new AccountId(entry.accountId()))
                .orElseThrow(() -> new InvalidRefreshTokenException("account not found: " + entry.accountId()));
        String newAccessToken = accessTokenIssuerPort.issue(account.getId(), account.getUsername().value());

        return new RefreshResult(newAccessToken, newRefreshToken);
    }
}
