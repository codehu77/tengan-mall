package com.tengan.mall.admin.application.login;

import com.tengan.mall.admin.application.port.AdminAccessTokenIssuerPort;
import com.tengan.mall.admin.application.port.AdminRefreshTokenStorePort;
import com.tengan.mall.admin.domain.exception.AdminAccountDisabledException;
import com.tengan.mall.admin.domain.exception.InvalidAdminCredentialsException;
import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.PermissionCode;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginService implements AdminLoginUseCase {

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminAccessTokenIssuerPort accessTokenIssuerPort;
    private final AdminRefreshTokenStorePort refreshTokenStorePort;

    public AdminLoginService(AdminUserRepository adminUserRepository, RoleRepository roleRepository,
            MenuRepository menuRepository, PasswordEncoder passwordEncoder,
            AdminAccessTokenIssuerPort accessTokenIssuerPort, AdminRefreshTokenStorePort refreshTokenStorePort) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
    }

    @Override
    public AdminLoginResult login(AdminLoginCommand command) {
        AdminUser adminUser = adminUserRepository.findByUsername(command.username())
                .orElseThrow(InvalidAdminCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), adminUser.getPasswordHash())) {
            throw new InvalidAdminCredentialsException();
        }
        if (!adminUser.isActive()) {
            throw new AdminAccountDisabledException();
        }

        List<Role> roles = roleRepository.findByIds(adminUser.getRoleIds());
        List<String> roleCodes = roles.stream().map(role -> role.getRoleCode().value()).toList();

        Set<MenuId> grantedMenuIds = menuRepository.findGrantedMenuIds(adminUser.getRoleIds());
        List<String> permissions = menuRepository.findAll().stream()
                .filter(menu -> grantedMenuIds.contains(menu.getId()))
                .map(menu -> menu.getPermissionCode().map(PermissionCode::value).orElse(null))
                .filter(code -> code != null)
                .distinct()
                .toList();

        String username = adminUser.getUsername().value();
        String accessToken = accessTokenIssuerPort.issue(adminUser.getId(), username, permissions);
        String refreshToken = refreshTokenStorePort.issue(adminUser.getId().value());

        return new AdminLoginResult(accessToken, refreshToken, adminUser.getId().value(), username,
                adminUser.getRealName(), roleCodes, permissions);
    }
}
