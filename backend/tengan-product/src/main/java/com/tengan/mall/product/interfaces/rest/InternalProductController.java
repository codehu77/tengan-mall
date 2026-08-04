package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.jwt.IdentityAssertionVerifier;
import com.tengan.mall.product.application.category.CategoryTreeNode;
import com.tengan.mall.product.application.category.CreateCategoryCommand;
import com.tengan.mall.product.application.category.CreateCategoryUseCase;
import com.tengan.mall.product.application.category.DeleteCategoryCommand;
import com.tengan.mall.product.application.category.DeleteCategoryUseCase;
import com.tengan.mall.product.application.category.HideCategoryCommand;
import com.tengan.mall.product.application.category.HideCategoryUseCase;
import com.tengan.mall.product.application.category.ListCategoriesUseCase;
import com.tengan.mall.product.application.category.ShowCategoryCommand;
import com.tengan.mall.product.application.category.ShowCategoryUseCase;
import com.tengan.mall.product.application.category.UpdateCategoryCommand;
import com.tengan.mall.product.application.category.UpdateCategoryUseCase;
import com.tengan.mall.product.interfaces.rest.dto.CategoryTreeNodeResponse;
import com.tengan.mall.product.interfaces.rest.dto.CategoryTreeResponse;
import com.tengan.mall.product.interfaces.rest.dto.CreateCategoryRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateCategoryResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateCategoryRequest;
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
@RequestMapping("/internal/products")
public class InternalProductController {

    private final ListCategoriesUseCase listCategoriesUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ShowCategoryUseCase showCategoryUseCase;
    private final HideCategoryUseCase hideCategoryUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalProductController(ListCategoriesUseCase listCategoriesUseCase,
            CreateCategoryUseCase createCategoryUseCase, UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase, ShowCategoryUseCase showCategoryUseCase,
            HideCategoryUseCase hideCategoryUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.createCategoryUseCase = createCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.showCategoryUseCase = showCategoryUseCase;
        this.hideCategoryUseCase = hideCategoryUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public CategoryTreeResponse categories() {
        var items = listCategoriesUseCase.list().items().stream().map(this::toResponse).toList();
        return new CategoryTreeResponse(items);
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateCategoryResponse> create(
            @RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateCategoryRequest request) {
        var result = createCategoryUseCase.create(new CreateCategoryCommand(operator(identityAssertion),
                request.parentId(), request.name(), request.icon(), request.sort()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCategoryResponse(result.id()));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        updateCategoryUseCase.update(new UpdateCategoryCommand(operator(identityAssertion), id, request.name(),
                request.icon(), request.sort()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> delete(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        deleteCategoryUseCase.delete(new DeleteCategoryCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/categories/{id}/show")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> show(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        showCategoryUseCase.show(new ShowCategoryCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/categories/{id}/hide")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> hide(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        hideCategoryUseCase.hide(new HideCategoryCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }

    private CategoryTreeNodeResponse toResponse(CategoryTreeNode node) {
        var children = node.children().stream().map(this::toResponse).toList();
        return new CategoryTreeNodeResponse(node.id(), node.name(), node.icon(), node.sort(), node.status(),
                children);
    }
}
