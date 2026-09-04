package com.tengan.mall.auth.application.oauth;

import com.tengan.mall.auth.application.port.OAuthUserInfo;
import com.tengan.mall.auth.domain.exception.OAuthBindingConflictException;
import com.tengan.mall.auth.domain.model.AccountOperLog;
import com.tengan.mall.auth.domain.model.OAuthProvider;
import com.tengan.mall.auth.domain.repository.AccountOauthBindingRepository;
import com.tengan.mall.auth.domain.repository.AccountOperLogRepository;
import com.tengan.mall.auth.infrastructure.security.LineOAuthProviderVerifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 已登入狀態下自助連結 LINE 帳號，邏輯跟 {@link LinkGoogleAccountService}/
 * {@link LinkFacebookAccountService} 對稱——已有 session、身份確定，不需要 email 比對猜測，直接查該
 * LINE 使用者 id 有沒有綁在別人身上即可，不需要走 OTP/密碼二次驗證。
 */
@Service
public class LinkLineAccountService implements LinkLineAccountUseCase {

    private final LineOAuthProviderVerifier lineOAuthProviderVerifier;
    private final AccountOauthBindingRepository accountOauthBindingRepository;
    private final AccountOperLogRepository accountOperLogRepository;

    public LinkLineAccountService(LineOAuthProviderVerifier lineOAuthProviderVerifier,
            AccountOauthBindingRepository accountOauthBindingRepository,
            AccountOperLogRepository accountOperLogRepository) {
        this.lineOAuthProviderVerifier = lineOAuthProviderVerifier;
        this.accountOauthBindingRepository = accountOauthBindingRepository;
        this.accountOperLogRepository = accountOperLogRepository;
    }

    @Override
    @Transactional
    public void link(LinkLineAccountCommand command) {
        OAuthUserInfo userInfo = lineOAuthProviderVerifier.verify(command.code(), command.nonce());

        var boundAccountId = accountOauthBindingRepository
                .findAccountIdByProviderAndProviderUserId(OAuthProvider.LINE, userInfo.providerUserId());
        if (boundAccountId.isPresent() && !boundAccountId.get().equals(command.accountId())) {
            throw new OAuthBindingConflictException("此 LINE 帳號已綁定其他會員");
        }
        if (accountOauthBindingRepository.existsByAccountIdAndProvider(command.accountId(), OAuthProvider.LINE)) {
            throw new OAuthBindingConflictException("您已綁定過 LINE 帳號");
        }

        accountOauthBindingRepository.save(command.accountId(), OAuthProvider.LINE, userInfo.providerUserId());
        accountOperLogRepository.save(AccountOperLog.create(String.valueOf(command.accountId()), "account",
                "link_oauth", "連結 LINE 帳號 id=" + userInfo.providerUserId()));
    }
}
