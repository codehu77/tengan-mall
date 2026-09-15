package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateBaseAttrStandardValuePayload;
import com.tengan.mall.admin.application.port.ProductBaseAttrStandardValuePort;
import com.tengan.mall.admin.application.port.UpdateBaseAttrStandardValuePayload;
import com.tengan.mall.admin.interfaces.rest.dto.BaseAttrStandardValueResponse;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBaseAttrStandardValueRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBaseAttrStandardValueResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListBaseAttrStandardValuesResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateBaseAttrStandardValueRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-product 的標準聚合值 internal 端點，跟 {@link ProductBaseAttrController} 同樣的純代理原則。 */
@RestController
public class ProductBaseAttrStandardValueController {

    private final ProductBaseAttrStandardValuePort port;

    public ProductBaseAttrStandardValueController(ProductBaseAttrStandardValuePort port) {
        this.port = port;
    }

    @GetMapping("/api/admin/products/base-attrs/standard-values")
    @PreAuthorize("hasAuthority('product:baseattr:read')")
    public ListBaseAttrStandardValuesResponse listByCategory(@RequestParam Long categoryId) {
        return toListResponse(port.listByCategory(categoryId));
    }

    @GetMapping("/api/admin/products/base-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('product:baseattr:read')")
    public ListBaseAttrStandardValuesResponse listByAttr(@PathVariable Long attrId) {
        return toListResponse(port.listByAttr(attrId));
    }

    @PostMapping("/api/admin/products/base-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('product:baseattr:write')")
    public CreateBaseAttrStandardValueResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @PathVariable Long attrId, @Valid @RequestBody CreateBaseAttrStandardValueRequest request) {
        Long id = port.create(attrId, new CreateBaseAttrStandardValuePayload(request.label(), request.sort()),
                operatorJwt.getTokenValue());
        return new CreateBaseAttrStandardValueResponse(id);
    }

    @PutMapping("/api/admin/products/base-attr-standard-values/{id}")
    @PreAuthorize("hasAuthority('product:baseattr:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateBaseAttrStandardValueRequest request) {
        port.update(id, new UpdateBaseAttrStandardValuePayload(request.label(), request.enabled(), request.sort()),
                operatorJwt.getTokenValue());
    }

    private ListBaseAttrStandardValuesResponse toListResponse(
            java.util.List<com.tengan.mall.admin.application.port.BaseAttrStandardValueItem> items) {
        var responses = items.stream()
                .map(v -> new BaseAttrStandardValueResponse(v.id(), v.attrId(), v.label(), v.enabled(), v.sort()))
                .toList();
        return new ListBaseAttrStandardValuesResponse(responses);
    }
}
