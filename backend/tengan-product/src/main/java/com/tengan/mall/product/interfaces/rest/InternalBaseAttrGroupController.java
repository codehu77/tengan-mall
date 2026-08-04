package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.jwt.IdentityAssertionVerifier;
import com.tengan.mall.product.application.baseattrgroup.CreateBaseAttrGroupCommand;
import com.tengan.mall.product.application.baseattrgroup.CreateBaseAttrGroupUseCase;
import com.tengan.mall.product.application.baseattrgroup.DeleteBaseAttrGroupCommand;
import com.tengan.mall.product.application.baseattrgroup.DeleteBaseAttrGroupUseCase;
import com.tengan.mall.product.application.baseattrgroup.ListBaseAttrGroupsQuery;
import com.tengan.mall.product.application.baseattrgroup.ListBaseAttrGroupsUseCase;
import com.tengan.mall.product.application.baseattrgroup.UpdateBaseAttrGroupCommand;
import com.tengan.mall.product.application.baseattrgroup.UpdateBaseAttrGroupUseCase;
import com.tengan.mall.product.interfaces.rest.dto.BaseAttrGroupResponse;
import com.tengan.mall.product.interfaces.rest.dto.CreateBaseAttrGroupRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateBaseAttrGroupResponse;
import com.tengan.mall.product.interfaces.rest.dto.ListBaseAttrGroupsResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateBaseAttrGroupRequest;
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
@RequestMapping("/internal/products/base-attr-groups")
public class InternalBaseAttrGroupController {

    private final ListBaseAttrGroupsUseCase listBaseAttrGroupsUseCase;
    private final CreateBaseAttrGroupUseCase createBaseAttrGroupUseCase;
    private final UpdateBaseAttrGroupUseCase updateBaseAttrGroupUseCase;
    private final DeleteBaseAttrGroupUseCase deleteBaseAttrGroupUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalBaseAttrGroupController(ListBaseAttrGroupsUseCase listBaseAttrGroupsUseCase,
            CreateBaseAttrGroupUseCase createBaseAttrGroupUseCase,
            UpdateBaseAttrGroupUseCase updateBaseAttrGroupUseCase,
            DeleteBaseAttrGroupUseCase deleteBaseAttrGroupUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listBaseAttrGroupsUseCase = listBaseAttrGroupsUseCase;
        this.createBaseAttrGroupUseCase = createBaseAttrGroupUseCase;
        this.updateBaseAttrGroupUseCase = updateBaseAttrGroupUseCase;
        this.deleteBaseAttrGroupUseCase = deleteBaseAttrGroupUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListBaseAttrGroupsResponse list(@RequestParam Long categoryId) {
        var items = listBaseAttrGroupsUseCase.list(new ListBaseAttrGroupsQuery(categoryId)).items().stream()
                .map(g -> new BaseAttrGroupResponse(g.id(), g.categoryId(), g.name(), g.sort()))
                .toList();
        return new ListBaseAttrGroupsResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateBaseAttrGroupResponse> create(
            @RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateBaseAttrGroupRequest request) {
        var result = createBaseAttrGroupUseCase.create(
                new CreateBaseAttrGroupCommand(operator(identityAssertion), request.categoryId(), request.name(),
                        request.sort()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateBaseAttrGroupResponse(result.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateBaseAttrGroupRequest request) {
        updateBaseAttrGroupUseCase.update(
                new UpdateBaseAttrGroupCommand(operator(identityAssertion), id, request.name(), request.sort()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> delete(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        deleteBaseAttrGroupUseCase.delete(new DeleteBaseAttrGroupCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
