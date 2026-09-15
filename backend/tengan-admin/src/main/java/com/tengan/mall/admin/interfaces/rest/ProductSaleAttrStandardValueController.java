package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateSaleAttrStandardValuePayload;
import com.tengan.mall.admin.application.port.ProductSaleAttrStandardValuePort;
import com.tengan.mall.admin.application.port.UpdateSaleAttrStandardValuePayload;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSaleAttrStandardValueRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSaleAttrStandardValueResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListSaleAttrStandardValuesResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SaleAttrStandardValueResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateSaleAttrStandardValueRequest;
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

/** BFF：轉發到 tengan-product 的標準聚合值 internal 端點，跟 {@link ProductSaleAttrController} 同樣的純代理原則。 */
@RestController
public class ProductSaleAttrStandardValueController {

    private final ProductSaleAttrStandardValuePort port;

    public ProductSaleAttrStandardValueController(ProductSaleAttrStandardValuePort port) {
        this.port = port;
    }

    @GetMapping("/api/admin/products/sale-attrs/standard-values")
    @PreAuthorize("hasAuthority('product:saleattr:read')")
    public ListSaleAttrStandardValuesResponse listByCategory(@RequestParam Long categoryId) {
        return toListResponse(port.listByCategory(categoryId));
    }

    @GetMapping("/api/admin/products/sale-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('product:saleattr:read')")
    public ListSaleAttrStandardValuesResponse listByAttr(@PathVariable Long attrId) {
        return toListResponse(port.listByAttr(attrId));
    }

    @PostMapping("/api/admin/products/sale-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('product:saleattr:write')")
    public CreateSaleAttrStandardValueResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @PathVariable Long attrId, @Valid @RequestBody CreateSaleAttrStandardValueRequest request) {
        Long id = port.create(attrId, new CreateSaleAttrStandardValuePayload(request.label(), request.sort()),
                operatorJwt.getTokenValue());
        return new CreateSaleAttrStandardValueResponse(id);
    }

    @PutMapping("/api/admin/products/sale-attr-standard-values/{id}")
    @PreAuthorize("hasAuthority('product:saleattr:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateSaleAttrStandardValueRequest request) {
        port.update(id, new UpdateSaleAttrStandardValuePayload(request.label(), request.enabled(), request.sort()),
                operatorJwt.getTokenValue());
    }

    private ListSaleAttrStandardValuesResponse toListResponse(
            java.util.List<com.tengan.mall.admin.application.port.SaleAttrStandardValueItem> items) {
        var responses = items.stream()
                .map(v -> new SaleAttrStandardValueResponse(v.id(), v.attrId(), v.label(), v.enabled(), v.sort()))
                .toList();
        return new ListSaleAttrStandardValuesResponse(responses);
    }
}
