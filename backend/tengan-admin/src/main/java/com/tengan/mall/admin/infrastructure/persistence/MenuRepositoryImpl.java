package com.tengan.mall.admin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tengan.mall.admin.domain.model.Menu;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.PermissionCode;
import com.tengan.mall.admin.domain.model.RoleId;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    public MenuRepositoryImpl(MenuMapper menuMapper, RoleMenuMapper roleMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public Menu save(Menu menu) {
        MenuPO po = toPO(menu);
        if (menu.getId() == null) {
            menuMapper.insert(po);
            menu.assignId(new MenuId(po.getId()));
        } else {
            menuMapper.updateById(po);
        }
        return menu;
    }

    @Override
    public Optional<Menu> findById(MenuId id) {
        MenuPO po = menuMapper.selectById(id.value());
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<Menu> findAll() {
        return menuMapper.selectList(null).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(MenuId id) {
        menuMapper.deleteById(id.value());
    }

    @Override
    public boolean existsByParentId(MenuId parentId) {
        return menuMapper.exists(Wrappers.<MenuPO>lambdaQuery().eq(MenuPO::getParentId, parentId.value()));
    }

    @Override
    public Set<MenuId> findGrantedMenuIds(Set<RoleId> roleIds) {
        if (roleIds.isEmpty()) {
            return Set.of();
        }
        List<Long> roleIdValues = roleIds.stream().map(RoleId::value).toList();
        return roleMenuMapper.selectList(Wrappers.<RoleMenuPO>lambdaQuery().in(RoleMenuPO::getRoleId, roleIdValues))
                .stream()
                .map(link -> new MenuId(link.getMenuId()))
                .collect(Collectors.toSet());
    }

    private MenuPO toPO(Menu menu) {
        MenuPO po = new MenuPO();
        po.setId(menu.getId() == null ? null : menu.getId().value());
        po.setParentId(menu.getParentId().map(MenuId::value).orElse(0L));
        po.setMenuType(menu.getMenuType());
        po.setTitle(menu.getTitle());
        po.setPath(menu.getPath());
        po.setComponent(menu.getComponent());
        po.setRouteName(menu.getRouteName());
        po.setIcon(menu.getIcon());
        po.setPermissionCode(menu.getPermissionCode().map(PermissionCode::value).orElse(null));
        po.setSortOrder(menu.getSortOrder());
        po.setStatus(menu.getStatus());
        return po;
    }

    private Menu toDomain(MenuPO po) {
        MenuId parentId = po.getParentId() == null || po.getParentId() == 0L ? null : new MenuId(po.getParentId());
        PermissionCode permissionCode = po.getPermissionCode() == null || po.getPermissionCode().isBlank() ? null
                : new PermissionCode(po.getPermissionCode());
        return Menu.reconstitute(new MenuId(po.getId()), parentId, po.getMenuType(), po.getTitle(), po.getPath(),
                po.getComponent(), po.getRouteName(), po.getIcon(), permissionCode, po.getSortOrder(),
                po.getStatus());
    }
}
