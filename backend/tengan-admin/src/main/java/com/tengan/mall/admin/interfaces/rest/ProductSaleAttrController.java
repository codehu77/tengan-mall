package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateSaleAttrPayload;
import com.tengan.mall.admin.application.port.ProductSaleAttrPort;
import com.tengan.mall.admin.application.port.UpdateSaleAttrPayload;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSaleAttrRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSaleAttrResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListSaleAttrsResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SaleAttrResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateSaleAttrRequest;
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

/** BFF：轉發到 tengan-product 的 /internal/products/sale-attrs，跟 {@link ProductCategoryController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/products/sale-attrs")
public class ProductSaleAttrController {

    private final ProductSaleAttrPort productSaleAttrPort;

    public ProductSaleAttrController(ProductSaleAttrPort productSaleAttrPort) {
        this.productSaleAttrPort = productSaleAttrPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:saleattr:read')")
    public ListSaleAttrsResponse list(@RequestParam Long categoryId) {
        var items = productSaleAttrPort.listSaleAttrs(categoryId).stream()
                .map(a -> new SaleAttrResponse(a.id(), a.categoryId(), a.name(), a.searchable(), a.sort()))
                .toList();
        return new ListSaleAttrsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:saleattr:write')")
    public CreateSaleAttrResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateSaleAttrRequest request) {
        Long id = productSaleAttrPort.createSaleAttr(
                new CreateSaleAttrPayload(request.categoryId(), request.name(), request.searchable(), request.sort()),
                operatorJwt.getTokenValue());
        return new CreateSaleAttrResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:saleattr:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateSaleAttrRequest request) {
        productSaleAttrPort.updateSaleAttr(id,
                new UpdateSaleAttrPayload(request.name(), request.searchable(), request.sort()),
                operatorJwt.getTokenValue());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:saleattr:write')")
    public void delete(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productSaleAttrPort.deleteSaleAttr(id, operatorJwt.getTokenValue());
    }
}
