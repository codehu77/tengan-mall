package com.tengan.mall.auth.domain.repository;

import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(AccountId id);

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    /** 供 tengan-admin 的會員列表批次組裝狀態用，不逐筆查（見 GetAccountStatusesService）。 */
    List<Account> findAllById(List<Long> ids);
}
