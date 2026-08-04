package com.tengan.mall.admin.application.adminuser;

import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class GetAdminUserDetailService implements GetAdminUserDetailUseCase {

    private final AdminUserRepository adminUserRepository;

    public GetAdminUserDetailService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public GetAdminUserDetailResult getDetail(GetAdminUserDetailQuery query) {
        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(query.id()))
                .orElseThrow(() -> new NoSuchElementException("admin user not found: " + query.id()));
        return new GetAdminUserDetailResult(adminUser.getId().value(), adminUser.getUsername().value(),
                adminUser.getRealName(), adminUser.getStatus().getValue(),
                adminUser.getRoleIds().stream().map(RoleId::value).toList());
    }
}
