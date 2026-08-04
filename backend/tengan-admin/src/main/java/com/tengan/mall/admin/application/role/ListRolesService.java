package com.tengan.mall.admin.application.role;

import com.tengan.mall.admin.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class ListRolesService implements ListRolesUseCase {

    private final RoleRepository roleRepository;

    public ListRolesService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public ListRolesResult list() {
        var items = roleRepository.findAll().stream()
                .map(role -> new RoleSummary(role.getId().value(), role.getRoleCode().value(), role.getRoleName(),
                        role.getStatus().getValue()))
                .toList();
        return new ListRolesResult(items);
    }
}
