package com.tengan.mall.admin.application.role;

import com.tengan.mall.admin.domain.exception.DuplicateRoleCodeException;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.model.RoleCode;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateRoleService implements CreateRoleUseCase {

    private final RoleRepository roleRepository;
    private final OperLogRepository operLogRepository;

    public CreateRoleService(RoleRepository roleRepository, OperLogRepository operLogRepository) {
        this.roleRepository = roleRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public CreateRoleResult create(CreateRoleCommand command) {
        if (roleRepository.existsByCode(command.roleCode())) {
            throw new DuplicateRoleCodeException(command.roleCode());
        }

        Role role = Role.create(new RoleCode(command.roleCode()), command.roleName());
        Role saved = roleRepository.save(role);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "role", "create",
                "新增角色 " + command.roleCode(), true));

        return new CreateRoleResult(saved.getId().value());
    }
}
