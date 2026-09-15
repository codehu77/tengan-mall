package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.jwt.IdentityAssertionVerifier;
import com.tengan.mall.product.application.baseattr.standardvalue.CreateBaseAttrStandardValueCommand;
import com.tengan.mall.product.application.baseattr.standardvalue.CreateBaseAttrStandardValueUseCase;
import com.tengan.mall.product.application.baseattr.standardvalue.ListBaseAttrStandardValuesByAttrQuery;
import com.tengan.mall.product.application.baseattr.standardvalue.ListBaseAttrStandardValuesByAttrUseCase;
import com.tengan.mall.product.application.baseattr.standardvalue.ListBaseAttrStandardValuesByCategoryQuery;
import com.tengan.mall.product.application.baseattr.standardvalue.ListBaseAttrStandardValuesByCategoryUseCase;
import com.tengan.mall.product.application.baseattr.standardvalue.UpdateBaseAttrStandardValueCommand;
import com.tengan.mall.product.application.baseattr.standardvalue.UpdateBaseAttrStandardValueUseCase;
import com.tengan.mall.product.interfaces.rest.dto.BaseAttrStandardValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.CreateBaseAttrStandardValueRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateBaseAttrStandardValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.ListBaseAttrStandardValuesResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateBaseAttrStandardValueRequest;
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
public class InternalBaseAttrStandardValueController {

    private final ListBaseAttrStandardValuesByAttrUseCase listByAttrUseCase;
    private final ListBaseAttrStandardValuesByCategoryUseCase listByCategoryUseCase;
    private final CreateBaseAttrStandardValueUseCase createUseCase;
    private final UpdateBaseAttrStandardValueUseCase updateUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalBaseAttrStandardValueController(ListBaseAttrStandardValuesByAttrUseCase listByAttrUseCase,
            ListBaseAttrStandardValuesByCategoryUseCase listByCategoryUseCase,
            CreateBaseAttrStandardValueUseCase createUseCase, UpdateBaseAttrStandardValueUseCase updateUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listByAttrUseCase = listByAttrUseCase;
        this.listByCategoryUseCase = listByCategoryUseCase;
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping("/internal/products/base-attrs/standard-values")
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListBaseAttrStandardValuesResponse listByCategory(@RequestParam Long categoryId) {
        var items = listByCategoryUseCase.list(new ListBaseAttrStandardValuesByCategoryQuery(categoryId)).items()
                .stream()
                .map(v -> new BaseAttrStandardValueResponse(v.id(), v.attrId(), v.label(), v.enabled(), v.sort()))
                .toList();
        return new ListBaseAttrStandardValuesResponse(items);
    }

    @GetMapping("/internal/products/base-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListBaseAttrStandardValuesResponse listByAttr(@PathVariable Long attrId) {
        var items = listByAttrUseCase.list(new ListBaseAttrStandardValuesByAttrQuery(attrId)).items().stream()
                .map(v -> new BaseAttrStandardValueResponse(v.id(), v.attrId(), v.label(), v.enabled(), v.sort()))
                .toList();
        return new ListBaseAttrStandardValuesResponse(items);
    }

    @PostMapping("/internal/products/base-attrs/{attrId}/standard-values")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateBaseAttrStandardValueResponse> create(
            @RequestHeader("X-Identity-Assertion") String identityAssertion, @PathVariable Long attrId,
            @Valid @RequestBody CreateBaseAttrStandardValueRequest request) {
        var result = createUseCase.create(new CreateBaseAttrStandardValueCommand(operator(identityAssertion), attrId,
                request.label(), request.sort()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateBaseAttrStandardValueResponse(result.id()));
    }

    @PutMapping("/internal/products/base-attr-standard-values/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateBaseAttrStandardValueRequest request) {
        updateUseCase.update(new UpdateBaseAttrStandardValueCommand(operator(identityAssertion), id, request.label(),
                request.enabled(), request.sort()));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
