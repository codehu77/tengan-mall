package com.tengan.mall.admin.application.me;

import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class GetMeService implements GetMeUseCase {

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;

    public GetMeService(AdminUserRepository adminUserRepository, RoleRepository roleRepository) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public GetMeResult getMe(GetMeQuery query) {
        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(query.adminId()))
                .orElseThrow(() -> new NoSuchElementException("admin user not found: " + query.adminId()));
        List<String> roleCodes = roleRepository.findByIds(adminUser.getRoleIds()).stream()
                .map(role -> role.getRoleCode().value())
                .toList();
        return new GetMeResult(adminUser.getId().value(), adminUser.getUsername().value(),
                adminUser.getRealName(), adminUser.getAvatarUrl(), roleCodes);
    }
}
