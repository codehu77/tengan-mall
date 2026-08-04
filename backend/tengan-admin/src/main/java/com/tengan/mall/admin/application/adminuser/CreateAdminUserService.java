package com.tengan.mall.admin.application.adminuser;

import com.tengan.mall.admin.domain.exception.DuplicateAdminUsernameException;
import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUsername;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateAdminUserService implements CreateAdminUserUseCase {

    private final AdminUserRepository adminUserRepository;
    private final OperLogRepository operLogRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateAdminUserService(AdminUserRepository adminUserRepository, OperLogRepository operLogRepository,
            PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.operLogRepository = operLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CreateAdminUserResult create(CreateAdminUserCommand command) {
        if (adminUserRepository.existsByUsername(command.username())) {
            throw new DuplicateAdminUsernameException(command.username());
        }

        AdminUser adminUser = AdminUser.create(new AdminUsername(command.username()),
                passwordEncoder.encode(command.password()), command.realName());
        AdminUser saved = adminUserRepository.save(adminUser);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "admin_user",
                "create", "新增管理員 " + command.username(), true));

        return new CreateAdminUserResult(saved.getId().value());
    }
}
