package com.tengan.mall.auth.application.account;

import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.AccountOperLog;
import com.tengan.mall.auth.domain.repository.AccountOperLogRepository;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 這是 account.status 真正被改動的地方——後台「停權會員」要擋住登入，靠的是這支端點，
 * 不是 tengan-member 那邊的 member.status（那張表只是 profile 顯示用，LoginService 完全不看它）。
 */
@Service
public class DisableAccountService implements DisableAccountUseCase {

    private final AccountRepository accountRepository;
    private final AccountOperLogRepository accountOperLogRepository;

    public DisableAccountService(AccountRepository accountRepository,
            AccountOperLogRepository accountOperLogRepository) {
        this.accountRepository = accountRepository;
        this.accountOperLogRepository = accountOperLogRepository;
    }

    @Override
    @Transactional
    public void disable(DisableAccountCommand command) {
        Account account = accountRepository.findById(new AccountId(command.accountId()))
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
        account.disable();
        accountRepository.save(account);

        accountOperLogRepository.save(AccountOperLog.create(command.operator(), "account", "disable",
                "停用帳號 id=" + command.accountId()));
    }
}
