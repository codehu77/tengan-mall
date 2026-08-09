package com.tengan.mall.admin.application.me;

import com.tengan.mall.admin.domain.exception.InvalidAdminCredentialsException;
import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangeMyPasswordService implements ChangeMyPasswordUseCase {

    private final AdminUserRepository adminUserRepository;
    private final OperLogRepository operLogRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangeMyPasswordService(AdminUserRepository adminUserRepository, OperLogRepository operLogRepository,
            PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.operLogRepository = operLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(ChangeMyPasswordCommand command) {
        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(command.adminId()))
                .orElseThrow(() -> new NoSuchElementException("admin user not found: " + command.adminId()));

        if (!passwordEncoder.matches(command.oldPassword(), adminUser.getPasswordHash())) {
            throw new InvalidAdminCredentialsException();
        }

        adminUser.changePassword(passwordEncoder.encode(command.newPassword()));
        adminUserRepository.save(adminUser);

        operLogRepository.save(OperLog.create(command.adminId(), adminUser.getUsername().value(), "admin_profile",
                "change_password", "修改密碼", true));
    }
}
