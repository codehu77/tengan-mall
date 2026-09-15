package com.tengan.mall.devtools.web;

import com.tengan.mall.devtools.client.ProductApiClient;
import com.tengan.mall.devtools.client.dto.BaseAttrList;
import com.tengan.mall.devtools.client.dto.BaseAttrStandardValueList;
import com.tengan.mall.devtools.client.dto.BrandList;
import com.tengan.mall.devtools.client.dto.CategoryTree;
import com.tengan.mall.devtools.client.dto.SaleAttrList;
import com.tengan.mall.devtools.client.dto.SaleAttrStandardValueList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 純轉發 tengan-product 既有的唯讀 internal 端點，給前端下拉選單用。 */
@RestController
@RequestMapping("/api")
public class LookupController {

    private final ProductApiClient productApiClient;

    public LookupController(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    @GetMapping("/categories")
    public CategoryTree categories() {
        return productApiClient.categories();
    }

    @GetMapping("/brands")
    public BrandList brands() {
        return productApiClient.brands();
    }

    @GetMapping("/categories/{categoryId}/base-attrs")
    public BaseAttrList baseAttrs(@PathVariable Long categoryId) {
        return productApiClient.baseAttrs(categoryId);
    }

    @GetMapping("/categories/{categoryId}/sale-attrs")
    public SaleAttrList saleAttrs(@PathVariable Long categoryId) {
        return productApiClient.saleAttrs(categoryId);
    }

    @GetMapping("/categories/{categoryId}/base-attr-standard-values")
    public BaseAttrStandardValueList baseAttrStandardValues(@PathVariable Long categoryId) {
        return productApiClient.baseAttrStandardValues(categoryId);
    }

    @GetMapping("/categories/{categoryId}/sale-attr-standard-values")
    public SaleAttrStandardValueList saleAttrStandardValues(@PathVariable Long categoryId) {
        return productApiClient.saleAttrStandardValues(categoryId);
    }
}
