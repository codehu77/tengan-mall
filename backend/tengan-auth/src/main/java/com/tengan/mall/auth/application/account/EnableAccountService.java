package com.tengan.mall.auth.application.account;

import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.AccountOperLog;
import com.tengan.mall.auth.domain.repository.AccountOperLogRepository;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnableAccountService implements EnableAccountUseCase {

    private final AccountRepository accountRepository;
    private final AccountOperLogRepository accountOperLogRepository;

    public EnableAccountService(AccountRepository accountRepository,
            AccountOperLogRepository accountOperLogRepository) {
        this.accountRepository = accountRepository;
        this.accountOperLogRepository = accountOperLogRepository;
    }

    @Override
    @Transactional
    public void enable(EnableAccountCommand command) {
        Account account = accountRepository.findById(new AccountId(command.accountId()))
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
        account.enable();
        accountRepository.save(account);

        accountOperLogRepository.save(AccountOperLog.create(command.operator(), "account", "enable",
                "啟用帳號 id=" + command.accountId()));
    }
}
