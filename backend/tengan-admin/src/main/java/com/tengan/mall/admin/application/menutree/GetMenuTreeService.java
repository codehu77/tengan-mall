package com.tengan.mall.admin.application.menutree;

import com.tengan.mall.admin.domain.model.AdminUser;
import com.tengan.mall.admin.domain.model.AdminUserId;
import com.tengan.mall.admin.domain.model.Menu;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.PermissionCode;
import com.tengan.mall.admin.domain.repository.AdminUserRepository;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 把「這個管理員的角色被授權的選單」組成 pure-admin 要的巢狀路由樹：CATALOG/MENU 當路由節點，
 * BUTTON 摺進父節點的 meta.auths，不當獨立路由子節點（見 docs/微服務後台API待開發清單.md
 * 「GET /api/admin/auth/menus」）。
 */
@Service
public class GetMenuTreeService implements GetMenuTreeUseCase {

    private static final Long ROOT_PARENT_ID = 0L;

    private final AdminUserRepository adminUserRepository;
    private final MenuRepository menuRepository;

    public GetMenuTreeService(AdminUserRepository adminUserRepository, MenuRepository menuRepository) {
        this.adminUserRepository = adminUserRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public GetMenuTreeResult getMenuTree(GetMenuTreeQuery query) {
        AdminUser adminUser = adminUserRepository.findById(new AdminUserId(query.adminId()))
                .orElseThrow(() -> new NoSuchElementException("admin user not found: " + query.adminId()));

        Set<MenuId> grantedMenuIds = menuRepository.findGrantedMenuIds(adminUser.getRoleIds());
        List<Menu> visible = menuRepository.findAll().stream()
                .filter(menu -> grantedMenuIds.contains(menu.getId()))
                .toList();

        Map<Long, List<Menu>> childrenByParent = visible.stream()
                .filter(menu -> !menu.isButton())
                .collect(Collectors.groupingBy(this::parentIdValue));

        Map<Long, List<String>> authsByParent = visible.stream()
                .filter(Menu::isButton)
                .filter(menu -> menu.getPermissionCode().isPresent())
                .collect(Collectors.groupingBy(this::parentIdValue,
                        Collectors.mapping(menu -> menu.getPermissionCode().map(PermissionCode::value).orElseThrow(),
                                Collectors.toList())));

        return new GetMenuTreeResult(buildNodes(ROOT_PARENT_ID, childrenByParent, authsByParent));
    }

    private long parentIdValue(Menu menu) {
        return menu.getParentId().map(MenuId::value).orElse(ROOT_PARENT_ID);
    }

    private List<MenuNode> buildNodes(Long parentId, Map<Long, List<Menu>> childrenByParent,
            Map<Long, List<String>> authsByParent) {
        return childrenByParent.getOrDefault(parentId, List.of()).stream()
                .sorted(Comparator.comparingInt(Menu::getSortOrder))
                .map(menu -> new MenuNode(
                        menu.getPath(),
                        menu.getRouteName(),
                        menu.getComponent(),
                        new MenuMeta(menu.getTitle(), menu.getIcon(), menu.getSortOrder(),
                                authsByParent.getOrDefault(menu.getId().value(), List.of())),
                        buildNodes(menu.getId().value(), childrenByParent, authsByParent)))
                .toList();
    }
}
