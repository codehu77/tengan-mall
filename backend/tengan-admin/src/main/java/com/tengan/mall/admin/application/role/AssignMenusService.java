package com.tengan.mall.admin.application.role;

import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AssignMenusService implements AssignMenusUseCase {

    private final RoleRepository roleRepository;
    private final OperLogRepository operLogRepository;

    public AssignMenusService(RoleRepository roleRepository, OperLogRepository operLogRepository) {
        this.roleRepository = roleRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public void assignMenus(AssignMenusCommand command) {
        Role role = roleRepository.findById(new RoleId(command.roleId()))
                .orElseThrow(() -> new NoSuchElementException("role not found: " + command.roleId()));

        Set<MenuId> menuIds = command.menuIds().stream().map(MenuId::new).collect(Collectors.toSet());
        role.grantMenus(menuIds);
        roleRepository.save(role);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "role",
                "assign_menus", "調整角色 " + role.getRoleCode().value() + " 的選單授權", true));
    }
}
