package com.tengan.mall.auth.application.account;

import com.tengan.mall.auth.domain.repository.AccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 供 tengan-admin 即時組裝會員列表用——tengan-member 的會員資料跟 tengan-auth 的登入狀態
 * 是兩件事，不在任一邊存複本，由呼叫端（tengan-admin）批次問這裡再合併顯示（見 2026-08
 * 「member 停權」設計討論，拿掉 member.status 複本後的做法）。
 */
@Service
public class GetAccountStatusesService implements GetAccountStatusesUseCase {

    private final AccountRepository accountRepository;

    public GetAccountStatusesService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountStatusItem> get(List<Long> accountIds) {
        return accountRepository.findAllById(accountIds).stream()
                .map(a -> new AccountStatusItem(a.getId().value(), a.getStatus().getValue()))
                .toList();
    }
}
