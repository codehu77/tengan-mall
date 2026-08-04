package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.adminuser.AssignRolesCommand;
import com.tengan.mall.admin.application.adminuser.AssignRolesUseCase;
import com.tengan.mall.admin.application.adminuser.CreateAdminUserCommand;
import com.tengan.mall.admin.application.adminuser.CreateAdminUserUseCase;
import com.tengan.mall.admin.application.adminuser.GetAdminUserDetailQuery;
import com.tengan.mall.admin.application.adminuser.GetAdminUserDetailUseCase;
import com.tengan.mall.admin.application.adminuser.ListAdminUsersQuery;
import com.tengan.mall.admin.application.adminuser.ListAdminUsersUseCase;
import com.tengan.mall.admin.application.adminuser.UpdateAdminUserStatusCommand;
import com.tengan.mall.admin.application.adminuser.UpdateAdminUserStatusUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.AdminUserDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.AdminUserItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.AssignRolesRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateAdminUserRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateAdminUserResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListAdminUsersResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateAdminUserStatusRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system/users")
public class AdminUserController {

    private final ListAdminUsersUseCase listAdminUsersUseCase;
    private final GetAdminUserDetailUseCase getAdminUserDetailUseCase;
    private final CreateAdminUserUseCase createAdminUserUseCase;
    private final UpdateAdminUserStatusUseCase updateAdminUserStatusUseCase;
    private final AssignRolesUseCase assignRolesUseCase;

    public AdminUserController(ListAdminUsersUseCase listAdminUsersUseCase,
            GetAdminUserDetailUseCase getAdminUserDetailUseCase, CreateAdminUserUseCase createAdminUserUseCase,
            UpdateAdminUserStatusUseCase updateAdminUserStatusUseCase, AssignRolesUseCase assignRolesUseCase) {
        this.listAdminUsersUseCase = listAdminUsersUseCase;
        this.getAdminUserDetailUseCase = getAdminUserDetailUseCase;
        this.createAdminUserUseCase = createAdminUserUseCase;
        this.updateAdminUserStatusUseCase = updateAdminUserStatusUseCase;
        this.assignRolesUseCase = assignRolesUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:user:read')")
    public ListAdminUsersResponse list(@RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        var result = listAdminUsersUseCase.list(new ListAdminUsersQuery(pageNum, pageSize));
        var items = result.items().stream()
                .map(item -> new AdminUserItemResponse(item.id(), item.username(), item.realName(), item.status()))
                .toList();
        return new ListAdminUsersResponse(items, result.total());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:read')")
    public AdminUserDetailResponse detail(@PathVariable Long id) {
        var result = getAdminUserDetailUseCase.getDetail(new GetAdminUserDetailQuery(id));
        return new AdminUserDetailResponse(result.id(), result.username(), result.realName(), result.status(),
                result.roleIds());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:write')")
    public CreateAdminUserResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateAdminUserRequest request) {
        var result = createAdminUserUseCase.create(new CreateAdminUserCommand(operatorId(operatorJwt),
                operatorUsername(operatorJwt), request.username(), request.password(), request.realName()));
        return new CreateAdminUserResponse(result.id());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:write')")
    public void updateStatus(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateAdminUserStatusRequest request) {
        updateAdminUserStatusUseCase.updateStatus(new UpdateAdminUserStatusCommand(operatorId(operatorJwt),
                operatorUsername(operatorJwt), id, request.active()));
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:write')")
    public void assignRoles(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody AssignRolesRequest request) {
        assignRolesUseCase.assignRoles(new AssignRolesCommand(operatorId(operatorJwt), operatorUsername(operatorJwt),
                id, request.roleIds()));
    }

    private Long operatorId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private String operatorUsername(Jwt jwt) {
        return jwt.getClaimAsString("username");
    }
}
