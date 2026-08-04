package com.tengan.mall.admin.application.menu;

import com.tengan.mall.admin.domain.model.Menu;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.model.PermissionCode;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class UpdateMenuService implements UpdateMenuUseCase {

    private final MenuRepository menuRepository;
    private final OperLogRepository operLogRepository;

    public UpdateMenuService(MenuRepository menuRepository, OperLogRepository operLogRepository) {
        this.menuRepository = menuRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    public void update(UpdateMenuCommand command) {
        Menu menu = menuRepository.findById(new MenuId(command.id()))
                .orElseThrow(() -> new NoSuchElementException("menu not found: " + command.id()));

        PermissionCode permissionCode = command.permissionCode() == null || command.permissionCode().isBlank()
                ? null
                : new PermissionCode(command.permissionCode());
        menu.update(command.title(), command.path(), command.component(), command.routeName(), command.icon(),
                permissionCode, command.sortOrder());
        menuRepository.save(menu);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "menu", "update",
                "修改選單 " + command.title(), true));
    }
}
