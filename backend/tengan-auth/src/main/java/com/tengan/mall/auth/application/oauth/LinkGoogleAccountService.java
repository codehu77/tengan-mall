package com.tengan.mall.auth.application.oauth;

import com.tengan.mall.auth.application.port.OAuthProviderVerifier;
import com.tengan.mall.auth.application.port.OAuthUserInfo;
import com.tengan.mall.auth.domain.exception.OAuthBindingConflictException;
import com.tengan.mall.auth.domain.model.AccountOperLog;
import com.tengan.mall.auth.domain.model.OAuthProvider;
import com.tengan.mall.auth.domain.repository.AccountOauthBindingRepository;
import com.tengan.mall.auth.domain.repository.AccountOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 已登入狀態下自助連結 Google 帳號（方向B「連動缺口」，見 login_register_redesign 定案）——
 * 因為已有 session、身份確定，不需要 email 比對猜測，直接查該 Google sub 有沒有綁在別人身上即可，
 * 不需要走 OTP/密碼二次驗證。
 */
@Service
public class LinkGoogleAccountService implements LinkGoogleAccountUseCase {

    private final OAuthProviderVerifier googleOAuthProviderVerifier;
    private final AccountOauthBindingRepository accountOauthBindingRepository;
    private final AccountOperLogRepository accountOperLogRepository;

    public LinkGoogleAccountService(OAuthProviderVerifier googleOAuthProviderVerifier,
            AccountOauthBindingRepository accountOauthBindingRepository,
            AccountOperLogRepository accountOperLogRepository) {
        this.googleOAuthProviderVerifier = googleOAuthProviderVerifier;
        this.accountOauthBindingRepository = accountOauthBindingRepository;
        this.accountOperLogRepository = accountOperLogRepository;
    }

    @Override
    @Transactional
    public void link(LinkGoogleAccountCommand command) {
        OAuthUserInfo userInfo = googleOAuthProviderVerifier.verify(command.idToken());

        var boundAccountId = accountOauthBindingRepository
                .findAccountIdByProviderAndProviderUserId(OAuthProvider.GOOGLE, userInfo.providerUserId());
        if (boundAccountId.isPresent() && !boundAccountId.get().equals(command.accountId())) {
            throw new OAuthBindingConflictException("此 Google 帳號已綁定其他會員");
        }
        if (accountOauthBindingRepository.existsByAccountIdAndProvider(command.accountId(), OAuthProvider.GOOGLE)) {
            throw new OAuthBindingConflictException("您已綁定過 Google 帳號");
        }

        accountOauthBindingRepository.save(command.accountId(), OAuthProvider.GOOGLE, userInfo.providerUserId());
        accountOperLogRepository.save(AccountOperLog.create(String.valueOf(command.accountId()), "account",
                "link_oauth", "連結 Google 帳號 sub=" + userInfo.providerUserId()));
    }
}
