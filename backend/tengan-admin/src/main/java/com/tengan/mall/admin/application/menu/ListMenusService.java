package com.tengan.mall.admin.application.menu;

import com.tengan.mall.admin.domain.model.Menu;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ListMenusService implements ListMenusUseCase {

    private static final Long ROOT_PARENT_ID = 0L;

    private final MenuRepository menuRepository;

    public ListMenusService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public ListMenusResult list() {
        Map<Long, List<Menu>> childrenByParent = menuRepository.findAll().stream()
                .collect(Collectors.groupingBy(menu -> menu.getParentId().map(MenuId::value).orElse(ROOT_PARENT_ID)));
        return new ListMenusResult(buildNodes(ROOT_PARENT_ID, childrenByParent));
    }

    private List<MenuTreeItem> buildNodes(Long parentId, Map<Long, List<Menu>> childrenByParent) {
        return childrenByParent.getOrDefault(parentId, List.of()).stream()
                .sorted(Comparator.comparingInt(Menu::getSortOrder))
                .map(menu -> new MenuTreeItem(
                        menu.getId().value(),
                        parentId,
                        menu.getMenuType().getValue(),
                        menu.getTitle(),
                        menu.getPath(),
                        menu.getComponent(),
                        menu.getRouteName(),
                        menu.getIcon(),
                        menu.getPermissionCode().map(pc -> pc.value()).orElse(null),
                        menu.getSortOrder(),
                        menu.getStatus().getValue(),
                        buildNodes(menu.getId().value(), childrenByParent)))
                .toList();
    }
}
