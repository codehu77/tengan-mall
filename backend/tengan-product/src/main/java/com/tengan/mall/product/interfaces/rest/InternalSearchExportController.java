package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.product.application.spu.SearchAttrPayload;
import com.tengan.mall.product.application.spu.SearchExportSpusUseCase;
import com.tengan.mall.product.interfaces.rest.dto.SearchAttrResponse;
import com.tengan.mall.product.interfaces.rest.dto.SearchExportResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuVariantResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuSearchDocumentResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 只給 tengan-search 全量重建索引用，語意上是「搜尋匯出」不是「Spu CRUD」，獨立開一個 controller，
 * 不塞進 InternalSpuController（比照既有「一個聚合根一個 controller」慣例的延伸）。
 */
@RestController
@RequestMapping("/internal/products/spus/search-export")
public class InternalSearchExportController {

    private final SearchExportSpusUseCase searchExportSpusUseCase;

    public InternalSearchExportController(SearchExportSpusUseCase searchExportSpusUseCase) {
        this.searchExportSpusUseCase = searchExportSpusUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public SearchExportResponse export(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize) {
        var result = searchExportSpusUseCase.export(pageNum, pageSize);
        var spus = result.spus().stream()
                .map(s -> new SpuSearchDocumentResponse(s.spuId(), s.spuName(), s.spuMainImage(), s.minPrice(),
                        s.maxPrice(), s.saleCount(), s.brandId(), s.brandName(), s.catalog1Id(), s.catalog1Name(),
                        s.catalog2Id(), s.catalog2Name(), s.catalog3Id(), s.catalog3Name(),
                        s.baseAttrs().stream().map(this::toAttrResponse).toList(),
                        s.skus().stream()
                                .map(v -> new SkuVariantResponse(v.skuId(), v.skuName(), v.price(), v.mainImage(),
                                        v.saleCount(), v.saleAttrs().stream().map(this::toAttrResponse).toList()))
                                .toList()))
                .toList();
        return new SearchExportResponse(spus, result.hasNext());
    }

    private SearchAttrResponse toAttrResponse(SearchAttrPayload a) {
        return new SearchAttrResponse(a.attrId(), a.attrType(), a.attrName(), a.attrValue());
    }
}
