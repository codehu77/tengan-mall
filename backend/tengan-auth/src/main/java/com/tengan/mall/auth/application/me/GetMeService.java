package com.tengan.mall.auth.application.me;

import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.OAuthProvider;
import com.tengan.mall.auth.domain.repository.AccountOauthBindingRepository;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class GetMeService implements GetMeUseCase {

    private final AccountRepository accountRepository;
    private final AccountOauthBindingRepository accountOauthBindingRepository;

    public GetMeService(AccountRepository accountRepository,
            AccountOauthBindingRepository accountOauthBindingRepository) {
        this.accountRepository = accountRepository;
        this.accountOauthBindingRepository = accountOauthBindingRepository;
    }

    @Override
    public GetMeResult getMe(GetMeQuery query) {
        Account account = accountRepository.findById(new AccountId(query.accountId()))
                .orElseThrow(() -> new NoSuchElementException("account not found: " + query.accountId()));
        boolean googleLinked = accountOauthBindingRepository
                .existsByAccountIdAndProvider(query.accountId(), OAuthProvider.GOOGLE);
        return new GetMeResult(account.getId().value(),
                account.getPhone() != null ? account.getPhone().value() : null,
                account.getEmail() != null ? account.getEmail().value() : null,
                googleLinked);
    }
}
