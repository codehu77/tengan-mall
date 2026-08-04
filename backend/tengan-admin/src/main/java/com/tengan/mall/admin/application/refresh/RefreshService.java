package com.tengan.mall.admin.application.refresh;

import com.tengan.mall.admin.application.port.AdminAccessTokenIssuerPort;
import com.tengan.mall.admin.application.port.AdminRefreshTokenEntry;
import com.tengan.mall.admin.application.port.AdminRefreshTokenStorePort;
import com.tengan.mall.admin.domain.exception.InvalidAdminRefreshTokenException;
import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.PermissionCode;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * Rotation + reuse detection（比照 tengan-auth 的 RefreshService）：已標記 used=true 的
 * refresh token 又被使用，代表它已被複製，撤銷整個 family 強制重新登入。
 */
@Service
public class RefreshService implements RefreshUseCase {

    private final AdminUserRepository adminUserRepository;
    private final MenuRepository menuRepository;
    private final AdminAccessTokenIssuerPort accessTokenIssuerPort;
    private final AdminRefreshTokenStorePort refreshTokenStorePort;

    public RefreshService(AdminUserRepository adminUserRepository, MenuRepository menuRepository,
            AdminAccessTokenIssuerPort accessTokenIssuerPort, AdminRefreshTokenStorePort refreshTokenStorePort) {
        this.adminUserRepository = adminUserRepository;
        this.menuRepository = menuRepository;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
    }

    @Override
    public RefreshResult refresh(RefreshCommand command) {
        AdminRefreshTokenEntry entry = refreshTokenStorePort.find(command.refreshToken())
                .orElseThrow(() -> new InvalidAdminRefreshTokenException("not found or expired"));

        if (entry.used()) {
            refreshTokenStorePort.revokeFamily(entry.familyId());
            throw new InvalidAdminRefreshTokenException("reuse detected, family revoked");
        }

        String newRefreshToken = refreshTokenStorePort.rotate(command.refreshToken(), entry);

        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(entry.adminUserId()))
                .orElseThrow(() -> new InvalidAdminRefreshTokenException("admin user not found: " + entry.adminUserId()));

        Set<MenuId> grantedMenuIds = menuRepository.findGrantedMenuIds(adminUser.getRoleIds());
        List<String> permissions = menuRepository.findAll().stream()
                .filter(menu -> grantedMenuIds.contains(menu.getId()))
                .map(menu -> menu.getPermissionCode().map(PermissionCode::value).orElse(null))
                .filter(code -> code != null)
                .distinct()
                .toList();

        String newAccessToken = accessTokenIssuerPort.issue(adminUser.getId(), adminUser.getUsername().value(),
                permissions);

        return new RefreshResult(newAccessToken, newRefreshToken);
    }
}
