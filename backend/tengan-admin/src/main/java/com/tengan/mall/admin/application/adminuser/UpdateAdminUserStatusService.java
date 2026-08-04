package com.tengan.mall.admin.application.adminuser;

import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class UpdateAdminUserStatusService implements UpdateAdminUserStatusUseCase {

    private final AdminUserRepository adminUserRepository;
    private final OperLogRepository operLogRepository;

    public UpdateAdminUserStatusService(AdminUserRepository adminUserRepository,
            OperLogRepository operLogRepository) {
        this.adminUserRepository = adminUserRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public void updateStatus(UpdateAdminUserStatusCommand command) {
        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(command.targetId()))
                .orElseThrow(() -> new NoSuchElementException("admin user not found: " + command.targetId()));

        if (command.active()) {
            adminUser.activate();
        } else {
            adminUser.disable();
        }
        adminUserRepository.save(adminUser);

        String action = command.active() ? "啟用" : "停用";
        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "admin_user",
                "update_status", action + "管理員 " + adminUser.getUsername().value(), true));
    }
}
