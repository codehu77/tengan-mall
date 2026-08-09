package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.login.AdminLoginCommand;
import com.tengan.mall.admin.application.login.AdminLoginUseCase;
import com.tengan.mall.admin.application.logout.LogoutCommand;
import com.tengan.mall.admin.application.logout.LogoutUseCase;
import com.tengan.mall.admin.application.me.ChangeMyPasswordCommand;
import com.tengan.mall.admin.application.me.ChangeMyPasswordUseCase;
import com.tengan.mall.admin.application.me.GetMeQuery;
import com.tengan.mall.admin.application.me.GetMeUseCase;
import com.tengan.mall.admin.application.me.UpdateMyProfileCommand;
import com.tengan.mall.admin.application.me.UpdateMyProfileUseCase;
import com.tengan.mall.admin.application.menutree.GetMenuTreeQuery;
import com.tengan.mall.admin.application.menutree.GetMenuTreeUseCase;
import com.tengan.mall.admin.application.menutree.MenuMeta;
import com.tengan.mall.admin.application.menutree.MenuNode;
import com.tengan.mall.admin.application.refresh.RefreshCommand;
import com.tengan.mall.admin.application.refresh.RefreshUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.ChangeMyPasswordRequest;
import com.tengan.mall.admin.interfaces.rest.dto.LoginRequest;
import com.tengan.mall.admin.interfaces.rest.dto.LoginResponse;
import com.tengan.mall.admin.interfaces.rest.dto.LogoutRequest;
import com.tengan.mall.admin.interfaces.rest.dto.MeResponse;
import com.tengan.mall.admin.interfaces.rest.dto.MenuMetaResponse;
import com.tengan.mall.admin.interfaces.rest.dto.MenuNodeResponse;
import com.tengan.mall.admin.interfaces.rest.dto.RefreshRequest;
import com.tengan.mall.admin.interfaces.rest.dto.RefreshResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateMyProfileRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminLoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshUseCase refreshUseCase;
    private final GetMeUseCase getMeUseCase;
    private final GetMenuTreeUseCase getMenuTreeUseCase;
    private final UpdateMyProfileUseCase updateMyProfileUseCase;
    private final ChangeMyPasswordUseCase changeMyPasswordUseCase;

    public AdminAuthController(AdminLoginUseCase loginUseCase, LogoutUseCase logoutUseCase,
            RefreshUseCase refreshUseCase, GetMeUseCase getMeUseCase, GetMenuTreeUseCase getMenuTreeUseCase,
            UpdateMyProfileUseCase updateMyProfileUseCase, ChangeMyPasswordUseCase changeMyPasswordUseCase) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.refreshUseCase = refreshUseCase;
        this.getMeUseCase = getMeUseCase;
        this.getMenuTreeUseCase = getMenuTreeUseCase;
        this.updateMyProfileUseCase = updateMyProfileUseCase;
        this.changeMyPasswordUseCase = changeMyPasswordUseCase;
    }

    /** 這支不驗 access token（見 SecurityConfig 的 unauthenticatedChain 註解），登入前本來就沒有 token。 */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var result = loginUseCase.login(new AdminLoginCommand(request.username(), request.password()));
        return new LoginResponse(result.accessToken(), result.refreshToken(), result.adminId(), result.username(),
                result.realName(), result.roleCodes(), result.permissions());
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        logoutUseCase.logout(new LogoutCommand(request.refreshToken()));
    }

    /** 這支也不驗 access token，驗的是 request body 這顆 refresh token。 */
    @PostMapping("/refresh")
    public RefreshResponse refresh(@Valid @RequestBody RefreshRequest request) {
        var result = refreshUseCase.refresh(new RefreshCommand(request.refreshToken()));
        return new RefreshResponse(result.accessToken(), result.refreshToken());
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt adminJwt) {
        Long adminId = Long.valueOf(adminJwt.getSubject());
        var result = getMeUseCase.getMe(new GetMeQuery(adminId));
        return new MeResponse(result.adminId(), result.username(), result.realName(), result.avatarUrl(),
                result.roleCodes());
    }

    @PutMapping("/me")
    public void updateMe(@AuthenticationPrincipal Jwt adminJwt, @Valid @RequestBody UpdateMyProfileRequest request) {
        Long adminId = Long.valueOf(adminJwt.getSubject());
        updateMyProfileUseCase.updateProfile(new UpdateMyProfileCommand(adminId, request.realName(),
                request.avatarUrl()));
    }

    @PutMapping("/me/password")
    public void changeMyPassword(@AuthenticationPrincipal Jwt adminJwt,
            @Valid @RequestBody ChangeMyPasswordRequest request) {
        Long adminId = Long.valueOf(adminJwt.getSubject());
        changeMyPasswordUseCase.changePassword(new ChangeMyPasswordCommand(adminId, request.oldPassword(),
                request.newPassword()));
    }

    @GetMapping("/menus")
    public List<MenuNodeResponse> menus(@AuthenticationPrincipal Jwt adminJwt) {
        Long adminId = Long.valueOf(adminJwt.getSubject());
        var result = getMenuTreeUseCase.getMenuTree(new GetMenuTreeQuery(adminId));
        return result.menus().stream().map(this::toResponse).toList();
    }

    private MenuNodeResponse toResponse(MenuNode node) {
        // 葉節點的 children 一定要在 JSON 裡完全省略（MenuNodeResponse 的 @JsonInclude(NON_EMPTY)
        // 負責這件事），這裡只要老實把空 list 傳進去就好，不用自己判斷要不要塞 null——
        // 詳細原因見 MenuNodeResponse 的 class doc。
        List<MenuNodeResponse> children = node.children().stream().map(this::toResponse).toList();
        return new MenuNodeResponse(node.path(), node.name(), node.component(), toResponse(node.meta()), children);
    }

    private MenuMetaResponse toResponse(MenuMeta meta) {
        return new MenuMetaResponse(meta.title(), meta.icon(), meta.rank(), meta.auths());
    }
}
