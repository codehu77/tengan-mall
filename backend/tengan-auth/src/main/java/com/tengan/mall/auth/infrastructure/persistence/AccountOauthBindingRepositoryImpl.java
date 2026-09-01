package com.tengan.mall.auth.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tengan.mall.auth.domain.model.OAuthProvider;
import com.tengan.mall.auth.domain.repository.AccountOauthBindingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AccountOauthBindingRepositoryImpl implements AccountOauthBindingRepository {

    private final AccountOauthBindingMapper mapper;

    public AccountOauthBindingRepositoryImpl(AccountOauthBindingMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Long> findAccountIdByProviderAndProviderUserId(OAuthProvider provider, String providerUserId) {
        AccountOauthBindingPO po = mapper.selectOne(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                .eq(AccountOauthBindingPO::getProvider, provider)
                .eq(AccountOauthBindingPO::getProviderUserId, providerUserId));
        return Optional.ofNullable(po).map(AccountOauthBindingPO::getAccountId);
    }

    @Override
    public boolean existsByAccountIdAndProvider(Long accountId, OAuthProvider provider) {
        return mapper.exists(Wrappers.<AccountOauthBindingPO>lambdaQuery()
                .eq(AccountOauthBindingPO::getAccountId, accountId)
                .eq(AccountOauthBindingPO::getProvider, provider));
    }

    @Override
    public void save(Long accountId, OAuthProvider provider, String providerUserId) {
        AccountOauthBindingPO po = new AccountOauthBindingPO();
        po.setAccountId(accountId);
        po.setProvider(provider);
        po.setProviderUserId(providerUserId);
        po.setCreatedAt(LocalDateTime.now());
        mapper.insert(po);
    }
}
