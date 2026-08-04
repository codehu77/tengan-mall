package com.tengan.mall.admin.domain.repository;

import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import java.util.List;
import java.util.Optional;

public interface AdminUserRepository {

    AdminUser save(AdminUser adminUser);

    Optional<AdminUser> findById(AdminUserId id);

    Optional<AdminUser> findByUsername(String username);

    boolean existsByUsername(String username);

    List<AdminUser> findPage(int pageNum, int pageSize);

    long count();
}
