package com.tengan.mall.admin.application.menu;

import com.tengan.mall.admin.domain.exception.InvalidMenuHierarchyException;
import com.tengan.mall.admin.domain.model.Menu;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.MenuType;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.model.PermissionCode;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CreateMenuService implements CreateMenuUseCase {

    private final MenuRepository menuRepository;
    private final OperLogRepository operLogRepository;

    public CreateMenuService(MenuRepository menuRepository, OperLogRepository operLogRepository) {
        this.menuRepository = menuRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public CreateMenuResult create(CreateMenuCommand command) {
        MenuId parentId = command.parentId() == null || command.parentId() == 0L ? null
                : new MenuId(command.parentId());
        MenuType menuType = MenuType.fromCode(command.menuType());

        MenuType parentType = null;
        if (parentId != null) {
            Menu parent = menuRepository.findById(parentId)
                    .orElseThrow(() -> new NoSuchElementException("parent menu not found: " + parentId.value()));
            parentType = parent.getMenuType();
        }
        validateHierarchy(parentType, menuType);

        PermissionCode permissionCode = command.permissionCode() == null || command.permissionCode().isBlank()
                ? null
                : new PermissionCode(command.permissionCode());

        Menu menu = Menu.create(parentId, menuType, command.title(), command.path(),
                command.component(), command.routeName(), command.icon(), permissionCode, command.sortOrder());
        Menu saved = menuRepository.save(menu);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "menu", "create",
                "新增選單 " + command.title(), true));

        return new CreateMenuResult(saved.getId().value());
    }

    /**
     * 頂層（無父節點）只能是目錄；目錄底下只能放選單；選單底下只能放按鈕；按鈕底下什麼都不能放。
     * 見 {@link InvalidMenuHierarchyException} 說明為什麼要卡這條規則。
     */
    private void validateHierarchy(MenuType parentType, MenuType childType) {
        boolean valid;
        if (parentType == null) {
            valid = childType == MenuType.CATALOG;
        } else if (parentType == MenuType.CATALOG) {
            valid = childType == MenuType.MENU;
        } else if (parentType == MenuType.MENU) {
            valid = childType == MenuType.BUTTON;
        } else {
            valid = false;
        }
        if (!valid) {
            throw new InvalidMenuHierarchyException(parentType, childType);
        }
    }
}
