package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.menu.CreateMenuCommand;
import com.tengan.mall.admin.application.menu.CreateMenuUseCase;
import com.tengan.mall.admin.application.menu.DeleteMenuCommand;
import com.tengan.mall.admin.application.menu.DeleteMenuUseCase;
import com.tengan.mall.admin.application.menu.ListMenusUseCase;
import com.tengan.mall.admin.application.menu.MenuTreeItem;
import com.tengan.mall.admin.application.menu.UpdateMenuCommand;
import com.tengan.mall.admin.application.menu.UpdateMenuUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.CreateMenuRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateMenuResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListMenusResponse;
import com.tengan.mall.admin.interfaces.rest.dto.MenuTreeItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateMenuRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system/menus")
public class AdminMenuController {

    private final ListMenusUseCase listMenusUseCase;
    private final CreateMenuUseCase createMenuUseCase;
    private final UpdateMenuUseCase updateMenuUseCase;
    private final DeleteMenuUseCase deleteMenuUseCase;

    public AdminMenuController(ListMenusUseCase listMenusUseCase, CreateMenuUseCase createMenuUseCase,
            UpdateMenuUseCase updateMenuUseCase, DeleteMenuUseCase deleteMenuUseCase) {
        this.listMenusUseCase = listMenusUseCase;
        this.createMenuUseCase = createMenuUseCase;
        this.updateMenuUseCase = updateMenuUseCase;
        this.deleteMenuUseCase = deleteMenuUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:read')")
    public ListMenusResponse list() {
        var items = listMenusUseCase.list().items().stream().map(this::toResponse).toList();
        return new ListMenusResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:write')")
    public CreateMenuResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateMenuRequest request) {
        var result = createMenuUseCase.create(new CreateMenuCommand(operatorId(operatorJwt),
                operatorUsername(operatorJwt), request.parentId(), request.menuType(), request.title(),
                request.path(), request.component(), request.routeName(), request.icon(),
                request.permissionCode(), request.sortOrder()));
        return new CreateMenuResponse(result.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateMenuRequest request) {
        updateMenuUseCase.update(new UpdateMenuCommand(operatorId(operatorJwt), operatorUsername(operatorJwt), id,
                request.title(), request.path(), request.component(), request.routeName(), request.icon(),
                request.permissionCode(), request.sortOrder()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:write')")
    public void delete(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        deleteMenuUseCase.delete(new DeleteMenuCommand(operatorId(operatorJwt), operatorUsername(operatorJwt), id));
    }

    private MenuTreeItemResponse toResponse(MenuTreeItem item) {
        return new MenuTreeItemResponse(item.id(), item.parentId(), item.menuType(), item.title(), item.path(),
                item.component(), item.routeName(), item.icon(), item.permissionCode(), item.sortOrder(),
                item.status(), item.children().stream().map(this::toResponse).toList());
    }

    private Long operatorId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private String operatorUsername(Jwt jwt) {
        return jwt.getClaimAsString("username");
    }
}
