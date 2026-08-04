package com.tengan.mall.admin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.model.RoleCode;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

    public RoleRepositoryImpl(RoleMapper roleMapper, RoleMenuMapper roleMenuMapper) {
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public Role save(Role role) {
        RolePO po = toPO(role);
        if (role.getId() == null) {
            roleMapper.insert(po);
            role.assignId(new RoleId(po.getId()));
        } else {
            roleMapper.updateById(po);
        }
        syncMenus(role.getId().value(), role.getMenuIds());
        return role;
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        RolePO po = roleMapper.selectById(id.value());
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<Role> findByIds(Set<RoleId> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<Long> idValues = ids.stream().map(RoleId::value).toList();
        return roleMapper.selectBatchIds(idValues).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Role> findByCode(String roleCode) {
        RolePO po = roleMapper.selectOne(Wrappers.<RolePO>lambdaQuery().eq(RolePO::getRoleCode, roleCode));
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String roleCode) {
        return roleMapper.exists(Wrappers.<RolePO>lambdaQuery().eq(RolePO::getRoleCode, roleCode));
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(null).stream().map(this::toDomain).toList();
    }

    @Override
    public void unassignMenuFromAllRoles(MenuId menuId) {
        roleMenuMapper.delete(Wrappers.<RoleMenuPO>lambdaQuery().eq(RoleMenuPO::getMenuId, menuId.value()));
    }

    private void syncMenus(Long roleId, Set<MenuId> menuIds) {
        roleMenuMapper.delete(Wrappers.<RoleMenuPO>lambdaQuery().eq(RoleMenuPO::getRoleId, roleId));
        for (MenuId menuId : menuIds) {
            RoleMenuPO link = new RoleMenuPO();
            link.setRoleId(roleId);
            link.setMenuId(menuId.value());
            roleMenuMapper.insert(link);
        }
    }

    private RolePO toPO(Role role) {
        RolePO po = new RolePO();
        po.setId(role.getId() == null ? null : role.getId().value());
        po.setRoleCode(role.getRoleCode().value());
        po.setRoleName(role.getRoleName());
        po.setStatus(role.getStatus());
        return po;
    }

    private Role toDomain(RolePO po) {
        List<RoleMenuPO> links = roleMenuMapper.selectList(
                Wrappers.<RoleMenuPO>lambdaQuery().eq(RoleMenuPO::getRoleId, po.getId()));
        Set<MenuId> menuIds = links.stream().map(link -> new MenuId(link.getMenuId())).collect(Collectors.toSet());
        return Role.reconstitute(new RoleId(po.getId()), new RoleCode(po.getRoleCode()), po.getRoleName(),
                po.getStatus(), menuIds);
    }
}
