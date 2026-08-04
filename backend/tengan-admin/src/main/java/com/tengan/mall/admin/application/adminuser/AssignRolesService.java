package com.tengan.mall.admin.application.adminuser;

import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AssignRolesService implements AssignRolesUseCase {

    private final AdminUserRepository adminUserRepository;
    private final OperLogRepository operLogRepository;

    public AssignRolesService(AdminUserRepository adminUserRepository, OperLogRepository operLogRepository) {
        this.adminUserRepository = adminUserRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public void assignRoles(AssignRolesCommand command) {
        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(command.targetId()))
                .orElseThrow(() -> new NoSuchElementException("admin user not found: " + command.targetId()));

        Set<RoleId> roleIds = command.roleIds().stream().map(RoleId::new).collect(Collectors.toSet());
        adminUser.replaceRoles(roleIds);
        adminUserRepository.save(adminUser);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "admin_user",
                "assign_roles", "調整管理員 " + adminUser.getUsername().value() + " 的角色", true));
    }
}
