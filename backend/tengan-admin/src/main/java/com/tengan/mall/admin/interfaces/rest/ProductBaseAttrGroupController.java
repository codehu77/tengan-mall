package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateBaseAttrGroupPayload;
import com.tengan.mall.admin.application.port.ProductBaseAttrGroupPort;
import com.tengan.mall.admin.application.port.UpdateBaseAttrGroupPayload;
import com.tengan.mall.admin.interfaces.rest.dto.BaseAttrGroupResponse;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBaseAttrGroupRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBaseAttrGroupResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListBaseAttrGroupsResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateBaseAttrGroupRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-product 的 /internal/products/base-attr-groups，跟 {@link ProductCategoryController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/products/base-attr-groups")
public class ProductBaseAttrGroupController {

    private final ProductBaseAttrGroupPort productBaseAttrGroupPort;

    public ProductBaseAttrGroupController(ProductBaseAttrGroupPort productBaseAttrGroupPort) {
        this.productBaseAttrGroupPort = productBaseAttrGroupPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:baseattrgroup:read')")
    public ListBaseAttrGroupsResponse list(@RequestParam Long categoryId) {
        var items = productBaseAttrGroupPort.listBaseAttrGroups(categoryId).stream()
                .map(g -> new BaseAttrGroupResponse(g.id(), g.categoryId(), g.name(), g.sort()))
                .toList();
        return new ListBaseAttrGroupsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:baseattrgroup:write')")
    public CreateBaseAttrGroupResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateBaseAttrGroupRequest request) {
        Long id = productBaseAttrGroupPort.createBaseAttrGroup(
                new CreateBaseAttrGroupPayload(request.categoryId(), request.name(), request.sort()),
                operatorJwt.getTokenValue());
        return new CreateBaseAttrGroupResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:baseattrgroup:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateBaseAttrGroupRequest request) {
        productBaseAttrGroupPort.updateBaseAttrGroup(id,
                new UpdateBaseAttrGroupPayload(request.name(), request.sort()), operatorJwt.getTokenValue());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:baseattrgroup:write')")
    public void delete(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productBaseAttrGroupPort.deleteBaseAttrGroup(id, operatorJwt.getTokenValue());
    }
}
