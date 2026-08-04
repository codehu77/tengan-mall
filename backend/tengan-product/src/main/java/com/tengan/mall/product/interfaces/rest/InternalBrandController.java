package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.jwt.IdentityAssertionVerifier;
import com.tengan.mall.product.application.brand.CreateBrandCommand;
import com.tengan.mall.product.application.brand.CreateBrandUseCase;
import com.tengan.mall.product.application.brand.DeleteBrandCommand;
import com.tengan.mall.product.application.brand.DeleteBrandUseCase;
import com.tengan.mall.product.application.brand.HideBrandCommand;
import com.tengan.mall.product.application.brand.HideBrandUseCase;
import com.tengan.mall.product.application.brand.ListBrandsUseCase;
import com.tengan.mall.product.application.brand.ShowBrandCommand;
import com.tengan.mall.product.application.brand.ShowBrandUseCase;
import com.tengan.mall.product.application.brand.UpdateBrandCommand;
import com.tengan.mall.product.application.brand.UpdateBrandUseCase;
import com.tengan.mall.product.interfaces.rest.dto.BrandResponse;
import com.tengan.mall.product.interfaces.rest.dto.CreateBrandRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateBrandResponse;
import com.tengan.mall.product.interfaces.rest.dto.ListBrandsResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateBrandRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products/brands")
public class InternalBrandController {

    private final ListBrandsUseCase listBrandsUseCase;
    private final CreateBrandUseCase createBrandUseCase;
    private final UpdateBrandUseCase updateBrandUseCase;
    private final DeleteBrandUseCase deleteBrandUseCase;
    private final ShowBrandUseCase showBrandUseCase;
    private final HideBrandUseCase hideBrandUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalBrandController(ListBrandsUseCase listBrandsUseCase, CreateBrandUseCase createBrandUseCase,
            UpdateBrandUseCase updateBrandUseCase, DeleteBrandUseCase deleteBrandUseCase,
            ShowBrandUseCase showBrandUseCase, HideBrandUseCase hideBrandUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listBrandsUseCase = listBrandsUseCase;
        this.createBrandUseCase = createBrandUseCase;
        this.updateBrandUseCase = updateBrandUseCase;
        this.deleteBrandUseCase = deleteBrandUseCase;
        this.showBrandUseCase = showBrandUseCase;
        this.hideBrandUseCase = hideBrandUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListBrandsResponse list() {
        var items = listBrandsUseCase.list().items().stream()
                .map(b -> new BrandResponse(b.id(), b.name(), b.logo(), b.descript(), b.firstLetter(), b.sort(),
                        b.status()))
                .toList();
        return new ListBrandsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateBrandResponse> create(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateBrandRequest request) {
        var result = createBrandUseCase.create(new CreateBrandCommand(operator(identityAssertion), request.name(),
                request.logo(), request.descript(), request.firstLetter(), request.sort()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateBrandResponse(result.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateBrandRequest request) {
        updateBrandUseCase.update(new UpdateBrandCommand(operator(identityAssertion), id, request.name(),
                request.logo(), request.descript(), request.firstLetter(), request.sort()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> delete(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        deleteBrandUseCase.delete(new DeleteBrandCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/show")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> show(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        showBrandUseCase.show(new ShowBrandCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/hide")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> hide(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        hideBrandUseCase.hide(new HideBrandCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
