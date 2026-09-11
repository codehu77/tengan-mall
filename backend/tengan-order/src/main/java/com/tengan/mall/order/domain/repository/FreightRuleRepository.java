package com.tengan.mall.order.domain.repository;

import com.tengan.mall.order.domain.model.FreightRule;

/** 單列設定表，id 固定為 1，migration 已預先塞好一筆預設值（見 V3 遷移檔）。 */
public interface FreightRuleRepository {

    FreightRule get();

    void update(FreightRule rule);
}
