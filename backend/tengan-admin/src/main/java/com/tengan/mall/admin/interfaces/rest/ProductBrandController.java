package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateBrandPayload;
import com.tengan.mall.admin.application.port.ProductBrandPort;
import com.tengan.mall.admin.application.port.UpdateBrandPayload;
import com.tengan.mall.admin.interfaces.rest.dto.BrandItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBrandRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBrandResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListBrandsResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateBrandRequest;
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

/** BFF：轉發到 tengan-product 的 /internal/products/brands，跟 {@link ProductCategoryController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/products/brands")
public class ProductBrandController {

    private final ProductBrandPort productBrandPort;

    public ProductBrandController(ProductBrandPort productBrandPort) {
        this.productBrandPort = productBrandPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:brand:read')")
    public ListBrandsResponse list() {
        var items = productBrandPort.listBrands().stream()
                .map(b -> new BrandItemResponse(b.id(), b.name(), b.logo(), b.descript(), b.firstLetter(), b.sort(),
                        b.status()))
                .toList();
        return new ListBrandsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:brand:write')")
    public CreateBrandResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateBrandRequest request) {
        Long id = productBrandPort.createBrand(new CreateBrandPayload(request.name(), request.logo(),
                request.descript(), request.firstLetter(), request.sort()), operatorJwt.getTokenValue());
        return new CreateBrandResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:brand:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateBrandRequest request) {
        productBrandPort.updateBrand(id, new UpdateBrandPayload(request.name(), request.logo(), request.descript(),
                request.firstLetter(), request.sort()), operatorJwt.getTokenValue());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:brand:write')")
    public void delete(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productBrandPort.deleteBrand(id, operatorJwt.getTokenValue());
    }

    @PutMapping("/{id}/show")
    @PreAuthorize("hasAuthority('product:brand:write')")
    public void show(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productBrandPort.showBrand(id, operatorJwt.getTokenValue());
    }

    @PutMapping("/{id}/hide")
    @PreAuthorize("hasAuthority('product:brand:write')")
    public void hide(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productBrandPort.hideBrand(id, operatorJwt.getTokenValue());
    }
}
