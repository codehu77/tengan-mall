package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateBaseAttrPayload;
import com.tengan.mall.admin.application.port.ProductBaseAttrPort;
import com.tengan.mall.admin.application.port.UpdateBaseAttrPayload;
import com.tengan.mall.admin.interfaces.rest.dto.BaseAttrResponse;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBaseAttrRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBaseAttrResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListBaseAttrsResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateBaseAttrRequest;
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

/** BFF：轉發到 tengan-product 的 /internal/products/base-attrs，跟 {@link ProductCategoryController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/products/base-attrs")
public class ProductBaseAttrController {

    private final ProductBaseAttrPort productBaseAttrPort;

    public ProductBaseAttrController(ProductBaseAttrPort productBaseAttrPort) {
        this.productBaseAttrPort = productBaseAttrPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:baseattr:read')")
    public ListBaseAttrsResponse list(@RequestParam Long categoryId) {
        var items = productBaseAttrPort.listBaseAttrs(categoryId).stream()
                .map(a -> new BaseAttrResponse(a.id(), a.categoryId(), a.attrGroupId(), a.name(), a.searchable(),
                        a.sort()))
                .toList();
        return new ListBaseAttrsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:baseattr:write')")
    public CreateBaseAttrResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateBaseAttrRequest request) {
        Long id = productBaseAttrPort.createBaseAttr(
                new CreateBaseAttrPayload(request.categoryId(), request.attrGroupId(), request.name(),
                        request.searchable(), request.sort()),
                operatorJwt.getTokenValue());
        return new CreateBaseAttrResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:baseattr:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateBaseAttrRequest request) {
        productBaseAttrPort.updateBaseAttr(id,
                new UpdateBaseAttrPayload(request.attrGroupId(), request.name(), request.searchable(),
                        request.sort()),
                operatorJwt.getTokenValue());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:baseattr:write')")
    public void delete(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productBaseAttrPort.deleteBaseAttr(id, operatorJwt.getTokenValue());
    }
}
