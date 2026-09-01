package com.tengan.mall.auth.domain.repository;

import com.tengan.mall.auth.domain.model.OAuthProvider;
import java.util.Optional;

/**
 * account_oauth_binding 是純關聯查詢表，不是獨立聚合根（比照 role_menu 這類 join table 的既有
 * 處理方式，見 V3 migration 註解），所以這裡直接操作 accountId/provider/providerUserId，
 * 不包一層聚合根物件。
 */
public interface AccountOauthBindingRepository {

    Optional<Long> findAccountIdByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    boolean existsByAccountIdAndProvider(Long accountId, OAuthProvider provider);

    void save(Long accountId, OAuthProvider provider, String providerUserId);
}
