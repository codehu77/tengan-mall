package com.tengan.mall.admin.application.role;

import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class UpdateRoleService implements UpdateRoleUseCase {

    private final RoleRepository roleRepository;
    private final OperLogRepository operLogRepository;

    public UpdateRoleService(RoleRepository roleRepository, OperLogRepository operLogRepository) {
        this.roleRepository = roleRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public void update(UpdateRoleCommand command) {
        Role role = roleRepository.findById(new RoleId(command.id()))
                .orElseThrow(() -> new NoSuchElementException("role not found: " + command.id()));

        role.rename(command.roleName());
        if (command.active()) {
            role.activate();
        } else {
            role.disable();
        }
        roleRepository.save(role);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "role", "update",
                "修改角色 " + role.getRoleCode().value(), true));
    }
}
