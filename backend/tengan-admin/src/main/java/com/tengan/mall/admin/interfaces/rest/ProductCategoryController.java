package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CategoryTreeItem;
import com.tengan.mall.admin.application.port.CreateCategoryPayload;
import com.tengan.mall.admin.application.port.ProductCategoryPort;
import com.tengan.mall.admin.application.port.UpdateCategoryPayload;
import com.tengan.mall.admin.interfaces.rest.dto.CategoryTreeItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.CategoryTreeResponse;
import com.tengan.mall.admin.interfaces.rest.dto.CreateCategoryRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateCategoryResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateCategoryRequest;
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

/**
 * BFF：轉發到 tengan-product 的 /internal/products/categories，不重做業務規則
 * （見 ProductCategoryPort 的說明）。
 */
@RestController
@RequestMapping("/api/admin/products/categories")
public class ProductCategoryController {

    private final ProductCategoryPort productCategoryPort;

    public ProductCategoryController(ProductCategoryPort productCategoryPort) {
        this.productCategoryPort = productCategoryPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:category:read')")
    public CategoryTreeResponse tree() {
        var items = productCategoryPort.listCategories().stream().map(this::toResponse).toList();
        return new CategoryTreeResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:category:write')")
    public CreateCategoryResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateCategoryRequest request) {
        Long id = productCategoryPort.createCategory(
                new CreateCategoryPayload(request.parentId(), request.name(), request.icon(), request.sort()),
                operatorJwt.getTokenValue());
        return new CreateCategoryResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:category:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        productCategoryPort.updateCategory(id,
                new UpdateCategoryPayload(request.name(), request.icon(), request.sort()),
                operatorJwt.getTokenValue());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:category:write')")
    public void delete(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productCategoryPort.deleteCategory(id, operatorJwt.getTokenValue());
    }

    @PutMapping("/{id}/show")
    @PreAuthorize("hasAuthority('product:category:write')")
    public void show(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productCategoryPort.showCategory(id, operatorJwt.getTokenValue());
    }

    @PutMapping("/{id}/hide")
    @PreAuthorize("hasAuthority('product:category:write')")
    public void hide(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productCategoryPort.hideCategory(id, operatorJwt.getTokenValue());
    }

    private CategoryTreeItemResponse toResponse(CategoryTreeItem item) {
        var children = item.children().stream().map(this::toResponse).toList();
        return new CategoryTreeItemResponse(item.id(), item.name(), item.icon(), item.sort(), item.status(),
                children);
    }
}
