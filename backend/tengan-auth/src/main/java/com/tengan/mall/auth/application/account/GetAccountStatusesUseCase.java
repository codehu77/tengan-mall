package com.tengan.mall.auth.application.account;

import java.util.List;

public interface GetAccountStatusesUseCase {

    List<AccountStatusItem> get(List<Long> accountIds);
}
