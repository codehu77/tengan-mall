package com.tengan.mall.auth.domain.repository;

import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(AccountId id);

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);
}
