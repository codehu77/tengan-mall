package com.tengan.mall.auth.domain.repository;

import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(AccountId id);

    /** identifier 是 phone 或 email，WHERE phone = ? OR email = ?（兩欄位都 UNIQUE，不會有歧義）。 */
    Optional<Account> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    /** 供 tengan-admin 的會員列表批次組裝狀態用，不逐筆查（見 GetAccountStatusesService）。 */
    List<Account> findAllById(List<Long> ids);
}
