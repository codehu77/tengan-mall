package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.jwt.IdentityAssertionVerifier;
import com.tengan.mall.product.application.saleattr.standardvalue.CreateSaleAttrStandardValueCommand;
import com.tengan.mall.product.application.saleattr.standardvalue.CreateSaleAttrStandardValueUseCase;
import com.tengan.mall.product.application.saleattr.standardvalue.ListSaleAttrStandardValuesByAttrQuery;
import com.tengan.mall.product.application.saleattr.standardvalue.ListSaleAttrStandardValuesByAttrUseCase;
import com.tengan.mall.product.application.saleattr.standardvalue.ListSaleAttrStandardValuesByCategoryQuery;
import com.tengan.mall.product.application.saleattr.standardvalue.ListSaleAttrStandardValuesByCategoryUseCase;
import com.tengan.mall.product.application.saleattr.standardvalue.UpdateSaleAttrStandardValueCommand;
import com.tengan.mall.product.application.saleattr.standardvalue.UpdateSaleAttrStandardValueUseCase;
import com.tengan.mall.product.interfaces.rest.dto.CreateSaleAttrStandardValueRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateSaleAttrStandardValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.ListSaleAttrStandardValuesResponse;
import com.tengan.mall.product.interfaces.rest.dto.SaleAttrStandardValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateSaleAttrStandardValueRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalSaleAttrStandardValueController {

    private final ListSaleAttrStandardValuesByAttrUseCase listByAttrUseCase;
    private final ListSaleAttrStandardValuesByCategoryUseCase listByCategoryUseCase;
    private final CreateSaleAttrStandardValueUseCase createUseCase;
    private final UpdateSaleAttrStandardValueUseCase updateUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalSaleAttrStandardValueController(ListSaleAttrStandardValuesByAttrUseCase listByAttrUseCase,
            ListSaleAttrStandardValuesByCategoryUseCase listByCategoryUseCase,
            CreateSaleAttrStandardValueUseCase createUseCase, UpdateSaleAttrStandardValueUseCase updateUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listByAttrUseCase = listByAttrUseCase;
        this.listByCategoryUseCase = listByCategoryUseCase;
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping("/internal/products/sale-attrs/standard-values")
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListSaleAttrStandardValuesResponse listByCategory(@RequestParam Long categoryId) {
        var items = listByCategoryUseCase.list(new ListSaleAttrStandardValuesByCategoryQuery(categoryId)).items()
                .stream()
                .map(v -> new SaleAttrStandardValueResponse(v.id(), v.attrId(), v.label(), v.enabled(), v.sort()))
                .toList();
        return new ListSaleAttrStandardValuesResponse(items);
    }

    @GetMapping("/internal/products/sale-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListSaleAttrStandardValuesResponse listByAttr(@PathVariable Long attrId) {
        var items = listByAttrUseCase.list(new ListSaleAttrStandardValuesByAttrQuery(attrId)).items().stream()
                .map(v -> new SaleAttrStandardValueResponse(v.id(), v.attrId(), v.label(), v.enabled(), v.sort()))
                .toList();
        return new ListSaleAttrStandardValuesResponse(items);
    }

    @PostMapping("/internal/products/sale-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateSaleAttrStandardValueResponse> create(
            @RequestHeader("X-Identity-Assertion") String identityAssertion, @PathVariable Long attrId,
            @Valid @RequestBody CreateSaleAttrStandardValueRequest request) {
        var result = createUseCase.create(new CreateSaleAttrStandardValueCommand(operator(identityAssertion), attrId,
                request.label(), request.sort()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateSaleAttrStandardValueResponse(result.id()));
    }

    @PutMapping("/internal/products/sale-attr-standard-values/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateSaleAttrStandardValueRequest request) {
        updateUseCase.update(new UpdateSaleAttrStandardValueCommand(operator(identityAssertion), id, request.label(),
                request.enabled(), request.sort()));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
