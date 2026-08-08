package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.product.application.spu.BatchGetSkuDetailsUseCase;
import com.tengan.mall.product.interfaces.rest.dto.SkuDetailResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuImageResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuSaleAttrValueResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 跟 InternalSpuController 分開獨立 controller（比照 Category/Brand 各自獨立的既有慣例），
 * 因為這支端點查的是 Sku 本身（走 SkuDetailPort 的 CQRS-lite 路徑），不是 Spu 聚合根。
 */
@RestController
@RequestMapping("/internal/products/skus")
public class InternalSkuController {

    private final BatchGetSkuDetailsUseCase batchGetSkuDetailsUseCase;

    public InternalSkuController(BatchGetSkuDetailsUseCase batchGetSkuDetailsUseCase) {
        this.batchGetSkuDetailsUseCase = batchGetSkuDetailsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public List<SkuDetailResponse> batchGet(@RequestParam List<Long> ids) {
        return batchGetSkuDetailsUseCase.get(ids).stream()
                .map(s -> new SkuDetailResponse(s.id(), s.spuId(), s.name(), s.price(), s.mainImage(), s.saleCount(),
                        s.sort(),
                        s.images().stream().map(i -> new SkuImageResponse(i.imageUrl(), i.sort())).toList(),
                        s.saleAttrValues().stream()
                                .map(v -> new SkuSaleAttrValueResponse(v.attrId(), v.attrName(), v.attrValue()))
                                .toList()))
                .toList();
    }
}
