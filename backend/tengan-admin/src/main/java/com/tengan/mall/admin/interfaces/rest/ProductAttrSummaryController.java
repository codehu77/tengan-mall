package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.ProductAttrSummaryPort;
import com.tengan.mall.admin.interfaces.rest.dto.CategoriesWithAttrsResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-product 的 /internal/products/categories/with-attrs，給左側分類樹畫提醒用。 */
@RestController
@RequestMapping("/api/admin/products/categories/with-attrs")
public class ProductAttrSummaryController {

    private final ProductAttrSummaryPort productAttrSummaryPort;

    public ProductAttrSummaryController(ProductAttrSummaryPort productAttrSummaryPort) {
        this.productAttrSummaryPort = productAttrSummaryPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:baseattrgroup:read')")
    public CategoriesWithAttrsResponse list() {
        return new CategoriesWithAttrsResponse(productAttrSummaryPort.listCategoriesWithAttrs());
    }
}
