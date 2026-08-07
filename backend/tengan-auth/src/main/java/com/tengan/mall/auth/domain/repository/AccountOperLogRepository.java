package com.tengan.mall.auth.domain.repository;

import com.tengan.mall.auth.domain.model.AccountOperLog;

/** 只有 save——比照 tengan-product/tengan-member 的既有模式，這次沒有做查詢/列表頁。 */
public interface AccountOperLogRepository {

    AccountOperLog save(AccountOperLog operLog);
}
