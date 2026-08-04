package com.tengan.mall.admin.application.menu;

import com.tengan.mall.admin.domain.exception.MenuHasChildrenException;
import com.tengan.mall.admin.domain.model.Menu;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.repository.MenuRepository;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import com.tengan.mall.admin.domain.repository.RoleRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteMenuService implements DeleteMenuUseCase {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final OperLogRepository operLogRepository;

    public DeleteMenuService(MenuRepository menuRepository, RoleRepository roleRepository,
            OperLogRepository operLogRepository) {
        this.menuRepository = menuRepository;
        this.roleRepository = roleRepository;
        this.operLogRepository = operLogRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteMenuCommand command) {
        MenuId id = new MenuId(command.id());
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("menu not found: " + command.id()));

        if (menuRepository.existsByParentId(id)) {
            throw new MenuHasChildrenException(command.id());
        }
        menuRepository.deleteById(id);
        roleRepository.unassignMenuFromAllRoles(id);

        operLogRepository.save(OperLog.create(command.operatorId(), command.operatorUsername(), "menu", "delete",
                "刪除選單 " + menu.getTitle(), true));
    }
}
