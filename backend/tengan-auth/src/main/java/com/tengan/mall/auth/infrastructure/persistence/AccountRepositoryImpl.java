package com.tengan.mall.auth.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.Phone;
import com.tengan.mall.auth.domain.model.Username;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMapper accountMapper;

    public AccountRepositoryImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public Account save(Account account) {
        AccountPO po = toPO(account);
        accountMapper.insert(po);
        account.assignId(new AccountId(po.getId()));
        return account;
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        AccountPO po = accountMapper.selectById(id.value());
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        AccountPO po = accountMapper.selectOne(
                Wrappers.<AccountPO>lambdaQuery().eq(AccountPO::getUsername, username));
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return accountMapper.exists(Wrappers.<AccountPO>lambdaQuery().eq(AccountPO::getUsername, username));
    }

    @Override
    public boolean existsByPhone(String phone) {
        return accountMapper.exists(Wrappers.<AccountPO>lambdaQuery().eq(AccountPO::getPhone, phone));
    }

    private AccountPO toPO(Account account) {
        AccountPO po = new AccountPO();
        po.setUsername(account.getUsername().value());
        po.setPhone(account.getPhone().value());
        po.setPasswordHash(account.getPasswordHash());
        po.setStatus(account.getStatus());
        return po;
    }

    private Account toDomain(AccountPO po) {
        return Account.reconstitute(
                new AccountId(po.getId()),
                new Username(po.getUsername()),
                new Phone(po.getPhone()),
                po.getPasswordHash(),
                po.getStatus());
    }
}
