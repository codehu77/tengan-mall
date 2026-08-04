package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.product.application.attrsummary.ListCategoriesWithAttrsUseCase;
import com.tengan.mall.product.interfaces.rest.dto.CategoriesWithAttrsResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products/categories/with-attrs")
public class InternalAttrSummaryController {

    private final ListCategoriesWithAttrsUseCase listCategoriesWithAttrsUseCase;

    public InternalAttrSummaryController(ListCategoriesWithAttrsUseCase listCategoriesWithAttrsUseCase) {
        this.listCategoriesWithAttrsUseCase = listCategoriesWithAttrsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public CategoriesWithAttrsResponse list() {
        return new CategoriesWithAttrsResponse(listCategoriesWithAttrsUseCase.list().categoryIds());
    }
}
