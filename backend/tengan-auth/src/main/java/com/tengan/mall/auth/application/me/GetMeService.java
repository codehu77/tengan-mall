package com.tengan.mall.auth.application.me;

import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class GetMeService implements GetMeUseCase {

    private final AccountRepository accountRepository;

    public GetMeService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public GetMeResult getMe(GetMeQuery query) {
        Account account = accountRepository.findById(new AccountId(query.accountId()))
                .orElseThrow(() -> new NoSuchElementException("account not found: " + query.accountId()));
        return new GetMeResult(account.getId().value(), account.getUsername().value(), account.getPhone().value());
    }
}
