package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.jwt.IdentityAssertionVerifier;
import com.tengan.mall.product.application.saleattr.CreateSaleAttrCommand;
import com.tengan.mall.product.application.saleattr.CreateSaleAttrUseCase;
import com.tengan.mall.product.application.saleattr.DeleteSaleAttrCommand;
import com.tengan.mall.product.application.saleattr.DeleteSaleAttrUseCase;
import com.tengan.mall.product.application.saleattr.ListSaleAttrsQuery;
import com.tengan.mall.product.application.saleattr.ListSaleAttrsUseCase;
import com.tengan.mall.product.application.saleattr.UpdateSaleAttrCommand;
import com.tengan.mall.product.application.saleattr.UpdateSaleAttrUseCase;
import com.tengan.mall.product.interfaces.rest.dto.CreateSaleAttrRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateSaleAttrResponse;
import com.tengan.mall.product.interfaces.rest.dto.ListSaleAttrsResponse;
import com.tengan.mall.product.interfaces.rest.dto.SaleAttrResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateSaleAttrRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products/sale-attrs")
public class InternalSaleAttrController {

    private final ListSaleAttrsUseCase listSaleAttrsUseCase;
    private final CreateSaleAttrUseCase createSaleAttrUseCase;
    private final UpdateSaleAttrUseCase updateSaleAttrUseCase;
    private final DeleteSaleAttrUseCase deleteSaleAttrUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalSaleAttrController(ListSaleAttrsUseCase listSaleAttrsUseCase,
            CreateSaleAttrUseCase createSaleAttrUseCase, UpdateSaleAttrUseCase updateSaleAttrUseCase,
            DeleteSaleAttrUseCase deleteSaleAttrUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listSaleAttrsUseCase = listSaleAttrsUseCase;
        this.createSaleAttrUseCase = createSaleAttrUseCase;
        this.updateSaleAttrUseCase = updateSaleAttrUseCase;
        this.deleteSaleAttrUseCase = deleteSaleAttrUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListSaleAttrsResponse list(@RequestParam Long categoryId) {
        var items = listSaleAttrsUseCase.list(new ListSaleAttrsQuery(categoryId)).items().stream()
                .map(a -> new SaleAttrResponse(a.id(), a.categoryId(), a.name(), a.searchable(), a.sort()))
                .toList();
        return new ListSaleAttrsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateSaleAttrResponse> create(
            @RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateSaleAttrRequest request) {
        var result = createSaleAttrUseCase.create(new CreateSaleAttrCommand(operator(identityAssertion),
                request.categoryId(), request.name(), request.searchable(), request.sort()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateSaleAttrResponse(result.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateSaleAttrRequest request) {
        updateSaleAttrUseCase.update(new UpdateSaleAttrCommand(operator(identityAssertion), id, request.name(),
                request.searchable(), request.sort()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> delete(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        deleteSaleAttrUseCase.delete(new DeleteSaleAttrCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
