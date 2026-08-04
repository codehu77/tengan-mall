package com.tengan.mall.admin.application.adminuser;

import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import org.springframework.stereotype.Service;

@Service
public class ListAdminUsersService implements ListAdminUsersUseCase {

    private final AdminUserRepository adminUserRepository;

    public ListAdminUsersService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public ListAdminUsersResult list(ListAdminUsersQuery query) {
        var items = adminUserRepository.findPage(query.pageNum(), query.pageSize()).stream()
                .map(u -> new AdminUserSummary(u.getId().value(), u.getUsername().value(), u.getRealName(),
                        u.getStatus().getValue()))
                .toList();
        return new ListAdminUsersResult(items, adminUserRepository.count());
    }
}
