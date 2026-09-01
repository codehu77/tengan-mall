package com.tengan.mall.auth.application.oauth;

import com.tengan.mall.auth.application.port.MemberRegisteredEventPublisherPort;
import com.tengan.mall.auth.application.port.OAuthProviderVerifier;
import com.tengan.mall.auth.application.port.OAuthUserInfo;
import com.tengan.mall.auth.application.session.IssueSessionService;
import com.tengan.mall.auth.application.session.SessionTokens;
import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.exception.EmailAlreadyRegisteredException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.Email;
import com.tengan.mall.auth.domain.model.OAuthProvider;
import com.tengan.mall.auth.domain.repository.AccountOauthBindingRepository;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Google 登入（GIS ID Token）。方向 B（見 login_register_redesign/oauth_login_design 定案，
 * 比照 Firebase/Auth0 預設行為）：email 相符時不自動合併，導使用者回登入頁，登入後才能在
 * 會員中心自助連結（{@link LinkGoogleAccountService}）。
 */
@Service
public class GoogleLoginService implements GoogleLoginUseCase {

    private final OAuthProviderVerifier googleOAuthProviderVerifier;
    private final AccountOauthBindingRepository accountOauthBindingRepository;
    private final AccountRepository accountRepository;
    private final MemberRegisteredEventPublisherPort memberRegisteredEventPublisherPort;
    private final IssueSessionService issueSessionService;

    public GoogleLoginService(OAuthProviderVerifier googleOAuthProviderVerifier,
            AccountOauthBindingRepository accountOauthBindingRepository, AccountRepository accountRepository,
            MemberRegisteredEventPublisherPort memberRegisteredEventPublisherPort,
            IssueSessionService issueSessionService) {
        this.googleOAuthProviderVerifier = googleOAuthProviderVerifier;
        this.accountOauthBindingRepository = accountOauthBindingRepository;
        this.accountRepository = accountRepository;
        this.memberRegisteredEventPublisherPort = memberRegisteredEventPublisherPort;
        this.issueSessionService = issueSessionService;
    }

    @Override
    @Transactional
    public GoogleLoginResult login(GoogleLoginCommand command) {
        OAuthUserInfo userInfo = googleOAuthProviderVerifier.verify(command.idToken());

        var existingAccountId = accountOauthBindingRepository
                .findAccountIdByProviderAndProviderUserId(OAuthProvider.GOOGLE, userInfo.providerUserId());

        Account account = existingAccountId.isPresent()
                ? loginExisting(existingAccountId.get())
                : registerNew(userInfo);

        SessionTokens tokens = issueSessionService.issue(account, true);
        return new GoogleLoginResult(account.getId().value(), tokens.accessToken(), tokens.refreshToken(),
                tokens.refreshTokenTtlSeconds());
    }

    private Account loginExisting(Long accountId) {
        return accountRepository.findById(new AccountId(accountId))
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private Account registerNew(OAuthUserInfo userInfo) {
        if (userInfo.emailVerified() && accountRepository.existsByIdentifier(userInfo.email())) {
            throw new EmailAlreadyRegisteredException(userInfo.email());
        }

        Email email = userInfo.emailVerified() ? new Email(userInfo.email()) : null;
        Account account = Account.createFromOAuth(null, email);
        Account saved = accountRepository.save(account);
        Long accountId = saved.getId().value();

        accountOauthBindingRepository.save(accountId, OAuthProvider.GOOGLE, userInfo.providerUserId());
        memberRegisteredEventPublisherPort.publish(accountId, null, email != null ? email.value() : null,
                userInfo.displayName(), userInfo.avatarUrl());

        return saved;
    }
}
