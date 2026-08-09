package com.tengan.mall.admin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.AdminUsername;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AdminUserRepositoryImpl implements AdminUserRepository {

    private final AdminUserMapper adminUserMapper;
    private final UserRoleMapper userRoleMapper;

    public AdminUserRepositoryImpl(AdminUserMapper adminUserMapper, UserRoleMapper userRoleMapper) {
        this.adminUserMapper = adminUserMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public AdminUser save(AdminUser adminUser) {
        AdminUserPO po = toPO(adminUser);
        if (adminUser.getId() == null) {
            adminUserMapper.insert(po);
            adminUser.assignId(new AdminUserId(po.getId()));
        } else {
            adminUserMapper.updateById(po);
        }
        syncRoles(adminUser.getId().value(), adminUser.getRoleIds());
        return adminUser;
    }

    @Override
    public Optional<AdminUser> findById(AdminUserId id) {
        AdminUserPO po = adminUserMapper.selectById(id.value());
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        AdminUserPO po = adminUserMapper.selectOne(
                Wrappers.<AdminUserPO>lambdaQuery().eq(AdminUserPO::getUsername, username));
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return adminUserMapper.exists(Wrappers.<AdminUserPO>lambdaQuery().eq(AdminUserPO::getUsername, username));
    }

    @Override
    public List<AdminUser> findPage(int pageNum, int pageSize) {
        Page<AdminUserPO> page = adminUserMapper.selectPage(new Page<>(pageNum, pageSize), null);
        return page.getRecords().stream().map(this::toDomain).toList();
    }

    @Override
    public long count() {
        return adminUserMapper.selectCount(null);
    }

    private void syncRoles(Long adminUserId, Set<RoleId> roleIds) {
        userRoleMapper.delete(Wrappers.<UserRolePO>lambdaQuery().eq(UserRolePO::getAdminUserId, adminUserId));
        for (RoleId roleId : roleIds) {
            UserRolePO link = new UserRolePO();
            link.setAdminUserId(adminUserId);
            link.setRoleId(roleId.value());
            userRoleMapper.insert(link);
        }
    }

    private AdminUserPO toPO(AdminUser adminUser) {
        AdminUserPO po = new AdminUserPO();
        po.setId(adminUser.getId() == null ? null : adminUser.getId().value());
        po.setUsername(adminUser.getUsername().value());
        po.setPasswordHash(adminUser.getPasswordHash());
        po.setRealName(adminUser.getRealName());
        po.setAvatarUrl(adminUser.getAvatarUrl());
        po.setStatus(adminUser.getStatus());
        return po;
    }

    private AdminUser toDomain(AdminUserPO po) {
        List<UserRolePO> links = userRoleMapper.selectList(
                Wrappers.<UserRolePO>lambdaQuery().eq(UserRolePO::getAdminUserId, po.getId()));
        Set<RoleId> roleIds = links.stream().map(link -> new RoleId(link.getRoleId())).collect(Collectors.toSet());
        return AdminUser.reconstitute(new AdminUserId(po.getId()), new AdminUsername(po.getUsername()),
                po.getPasswordHash(), po.getRealName(), po.getAvatarUrl(), po.getStatus(), roleIds);
    }
}
