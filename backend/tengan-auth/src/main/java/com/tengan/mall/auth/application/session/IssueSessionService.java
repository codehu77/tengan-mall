package com.tengan.mall.auth.application.session;

import com.tengan.mall.auth.application.port.AccessTokenIssuerPort;
import com.tengan.mall.auth.application.port.RefreshTokenStorePort;
import com.tengan.mall.auth.domain.exception.InvalidCredentialsException;
import com.tengan.mall.auth.domain.model.Account;
import org.springframework.stereotype.Service;

/**
 * 「完成登入」的共用最後一步——註冊完成/忘記密碼重設完成/OAuth 登入都會走到這裡（都視同
 * rememberMe=true），統一在這裡檢查 account.isActive()，五個入口自動吃到停權保護，不用各自
 * 寫一次。LoginService 的帳密登入不用這個服務——它的 isActive 檢查是刻意跟密碼比對合併成同一個
 * 防列舉分支，屬於不同的錯誤語意（統一回「帳號或密碼錯誤」），不適合套用這裡的檢查方式。
 */
@Service
public class IssueSessionService {

    private final AccessTokenIssuerPort accessTokenIssuerPort;
    private final RefreshTokenStorePort refreshTokenStorePort;

    public IssueSessionService(AccessTokenIssuerPort accessTokenIssuerPort,
            RefreshTokenStorePort refreshTokenStorePort) {
        this.accessTokenIssuerPort = accessTokenIssuerPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
    }

    public SessionTokens issue(Account account, boolean rememberMe) {
        if (!account.isActive()) {
            throw new InvalidCredentialsException();
        }

        String accessToken = accessTokenIssuerPort.issue(account.getId());
        String refreshToken = refreshTokenStorePort.issue(account.getId().value(), rememberMe);
        long ttlSeconds = refreshTokenStorePort.ttlSecondsFor(rememberMe);

        return new SessionTokens(accessToken, refreshToken, ttlSeconds);
    }
}
