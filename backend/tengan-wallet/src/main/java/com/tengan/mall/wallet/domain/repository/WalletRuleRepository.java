package com.tengan.mall.wallet.domain.repository;

import com.tengan.mall.wallet.domain.model.WalletRule;

/** 單列設定表，id 固定為 1，migration 已預先塞好一筆預設值（見 V1 遷移檔）。 */
public interface WalletRuleRepository {

    WalletRule get();

    void update(WalletRule rule);
}
