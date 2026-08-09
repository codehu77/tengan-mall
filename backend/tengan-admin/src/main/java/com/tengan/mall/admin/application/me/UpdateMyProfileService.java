package com.tengan.mall.admin.application.me;

import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class UpdateMyProfileService implements UpdateMyProfileUseCase {

    private final AdminUserRepository adminUserRepository;
    private final OperLogRepository operLogRepository;

    public UpdateMyProfileService(AdminUserRepository adminUserRepository, OperLogRepository operLogRepository) {
        this.adminUserRepository = adminUserRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public void updateProfile(UpdateMyProfileCommand command) {
        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(command.adminId()))
                .orElseThrow(() -> new NoSuchElementException("admin user not found: " + command.adminId()));

        adminUser.updateProfile(command.realName(), command.avatarUrl());
        adminUserRepository.save(adminUser);

        operLogRepository.save(OperLog.create(command.adminId(), adminUser.getUsername().value(), "admin_profile",
                "update", "更新個人資訊", true));
    }
}
