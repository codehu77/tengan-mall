package com.tengan.mall.admin.application.role;

import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class GetRoleDetailService implements GetRoleDetailUseCase {

    private final RoleRepository roleRepository;

    public GetRoleDetailService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public GetRoleDetailResult getDetail(GetRoleDetailQuery query) {
        Role role = roleRepository.findById(new RoleId(query.id()))
                .orElseThrow(() -> new NoSuchElementException("role not found: " + query.id()));
        return new GetRoleDetailResult(role.getId().value(), role.getRoleCode().value(), role.getRoleName(),
                role.getStatus().getValue(), role.getMenuIds().stream().map(MenuId::value).toList());
    }
}
