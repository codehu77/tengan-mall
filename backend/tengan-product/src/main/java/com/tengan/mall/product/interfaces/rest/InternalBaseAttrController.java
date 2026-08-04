package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.jwt.IdentityAssertionVerifier;
import com.tengan.mall.product.application.baseattr.CreateBaseAttrCommand;
import com.tengan.mall.product.application.baseattr.CreateBaseAttrUseCase;
import com.tengan.mall.product.application.baseattr.DeleteBaseAttrCommand;
import com.tengan.mall.product.application.baseattr.DeleteBaseAttrUseCase;
import com.tengan.mall.product.application.baseattr.ListBaseAttrsQuery;
import com.tengan.mall.product.application.baseattr.ListBaseAttrsUseCase;
import com.tengan.mall.product.application.baseattr.UpdateBaseAttrCommand;
import com.tengan.mall.product.application.baseattr.UpdateBaseAttrUseCase;
import com.tengan.mall.product.interfaces.rest.dto.BaseAttrResponse;
import com.tengan.mall.product.interfaces.rest.dto.CreateBaseAttrRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateBaseAttrResponse;
import com.tengan.mall.product.interfaces.rest.dto.ListBaseAttrsResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateBaseAttrRequest;
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
@RequestMapping("/internal/products/base-attrs")
public class InternalBaseAttrController {

    private final ListBaseAttrsUseCase listBaseAttrsUseCase;
    private final CreateBaseAttrUseCase createBaseAttrUseCase;
    private final UpdateBaseAttrUseCase updateBaseAttrUseCase;
    private final DeleteBaseAttrUseCase deleteBaseAttrUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalBaseAttrController(ListBaseAttrsUseCase listBaseAttrsUseCase,
            CreateBaseAttrUseCase createBaseAttrUseCase, UpdateBaseAttrUseCase updateBaseAttrUseCase,
            DeleteBaseAttrUseCase deleteBaseAttrUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listBaseAttrsUseCase = listBaseAttrsUseCase;
        this.createBaseAttrUseCase = createBaseAttrUseCase;
        this.updateBaseAttrUseCase = updateBaseAttrUseCase;
        this.deleteBaseAttrUseCase = deleteBaseAttrUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListBaseAttrsResponse list(@RequestParam Long categoryId) {
        var items = listBaseAttrsUseCase.list(new ListBaseAttrsQuery(categoryId)).items().stream()
                .map(a -> new BaseAttrResponse(a.id(), a.categoryId(), a.attrGroupId(), a.name(), a.searchable(),
                        a.sort()))
                .toList();
        return new ListBaseAttrsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateBaseAttrResponse> create(
            @RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateBaseAttrRequest request) {
        var result = createBaseAttrUseCase.create(new CreateBaseAttrCommand(operator(identityAssertion),
                request.categoryId(), request.attrGroupId(), request.name(), request.searchable(), request.sort()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateBaseAttrResponse(result.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateBaseAttrRequest request) {
        updateBaseAttrUseCase.update(new UpdateBaseAttrCommand(operator(identityAssertion), id,
                request.attrGroupId(), request.name(), request.searchable(), request.sort()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> delete(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        deleteBaseAttrUseCase.delete(new DeleteBaseAttrCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
