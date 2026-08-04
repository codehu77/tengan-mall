package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.role.AssignMenusCommand;
import com.tengan.mall.admin.application.role.AssignMenusUseCase;
import com.tengan.mall.admin.application.role.CreateRoleCommand;
import com.tengan.mall.admin.application.role.CreateRoleUseCase;
import com.tengan.mall.admin.application.role.GetRoleDetailQuery;
import com.tengan.mall.admin.application.role.GetRoleDetailUseCase;
import com.tengan.mall.admin.application.role.ListRolesUseCase;
import com.tengan.mall.admin.application.role.UpdateRoleCommand;
import com.tengan.mall.admin.application.role.UpdateRoleUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.AssignMenusRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateRoleRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateRoleResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListRolesResponse;
import com.tengan.mall.admin.interfaces.rest.dto.RoleDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.RoleItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateRoleRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system/roles")
public class AdminRoleController {

    private final ListRolesUseCase listRolesUseCase;
    private final GetRoleDetailUseCase getRoleDetailUseCase;
    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final AssignMenusUseCase assignMenusUseCase;

    public AdminRoleController(ListRolesUseCase listRolesUseCase, GetRoleDetailUseCase getRoleDetailUseCase,
            CreateRoleUseCase createRoleUseCase, UpdateRoleUseCase updateRoleUseCase,
            AssignMenusUseCase assignMenusUseCase) {
        this.listRolesUseCase = listRolesUseCase;
        this.getRoleDetailUseCase = getRoleDetailUseCase;
        this.createRoleUseCase = createRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.assignMenusUseCase = assignMenusUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:read')")
    public ListRolesResponse list() {
        var items = listRolesUseCase.list().items().stream()
                .map(item -> new RoleItemResponse(item.id(), item.roleCode(), item.roleName(), item.status()))
                .toList();
        return new ListRolesResponse(items);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:read')")
    public RoleDetailResponse detail(@PathVariable Long id) {
        var result = getRoleDetailUseCase.getDetail(new GetRoleDetailQuery(id));
        return new RoleDetailResponse(result.id(), result.roleCode(), result.roleName(), result.status(),
                result.menuIds());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:write')")
    public CreateRoleResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateRoleRequest request) {
        var result = createRoleUseCase.create(new CreateRoleCommand(operatorId(operatorJwt),
                operatorUsername(operatorJwt), request.roleCode(), request.roleName()));
        return new CreateRoleResponse(result.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        updateRoleUseCase.update(new UpdateRoleCommand(operatorId(operatorJwt), operatorUsername(operatorJwt), id,
                request.roleName(), request.active()));
    }

    @PutMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:write')")
    public void assignMenus(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody AssignMenusRequest request) {
        assignMenusUseCase.assignMenus(new AssignMenusCommand(operatorId(operatorJwt), operatorUsername(operatorJwt),
                id, request.menuIds()));
    }

    private Long operatorId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private String operatorUsername(Jwt jwt) {
        return jwt.getClaimAsString("username");
    }
}
