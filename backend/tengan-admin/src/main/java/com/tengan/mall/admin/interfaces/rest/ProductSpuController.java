package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateSpuPayload;
import com.tengan.mall.admin.application.port.ProductSpuPort;
import com.tengan.mall.admin.application.port.SkuImagePayload;
import com.tengan.mall.admin.application.port.SkuItem;
import com.tengan.mall.admin.application.port.SkuPayload;
import com.tengan.mall.admin.application.port.SkuSaleAttrValuePayload;
import com.tengan.mall.admin.application.port.SpuBaseAttrValuePayload;
import com.tengan.mall.admin.application.port.SpuDetailItem;
import com.tengan.mall.admin.application.port.SpuImageItem;
import com.tengan.mall.admin.application.port.SpuImagePayload;
import com.tengan.mall.admin.application.port.SpuSearchParams;
import com.tengan.mall.admin.application.port.UpdateSpuPayload;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSpuRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSpuResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListSpusResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SkuDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SkuImageRequest;
import com.tengan.mall.admin.interfaces.rest.dto.SkuImageResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SkuRequest;
import com.tengan.mall.admin.interfaces.rest.dto.SkuSaleAttrValueRequest;
import com.tengan.mall.admin.interfaces.rest.dto.SkuSaleAttrValueResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SpuBaseAttrValueRequest;
import com.tengan.mall.admin.interfaces.rest.dto.SpuBaseAttrValueResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SpuDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SpuImageRequest;
import com.tengan.mall.admin.interfaces.rest.dto.SpuImageResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SpuSummaryResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateSpuRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-product 的 /internal/products/spus，跟 {@link ProductCategoryController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/products/spus")
public class ProductSpuController {

    private final ProductSpuPort productSpuPort;

    public ProductSpuController(ProductSpuPort productSpuPort) {
        this.productSpuPort = productSpuPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:spu:read')")
    public ListSpusResponse search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        var result = productSpuPort
                .searchSpus(new SpuSearchParams(categoryId, brandId, name, status, pageNum, pageSize));
        var items = result.items().stream()
                .map(s -> new SpuSummaryResponse(s.id(), s.categoryId(), s.brandId(), s.name(), s.mainImage(),
                        s.status(), s.skuCount()))
                .toList();
        return new ListSpusResponse(items, result.total());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:spu:read')")
    public SpuDetailResponse get(@PathVariable Long id) {
        return toResponse(productSpuPort.getSpu(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:spu:write')")
    public CreateSpuResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateSpuRequest request) {
        Long id = productSpuPort.createSpu(
                new CreateSpuPayload(request.categoryId(), request.brandId(), request.name(), request.description(),
                        request.mainImage(), toAttrValuePayloads(request.attrValues()),
                        toSpuImagePayloads(request.images()), toSkuPayloads(request.skus())),
                operatorJwt.getTokenValue());
        return new CreateSpuResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:spu:write')")
    public void update(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateSpuRequest request) {
        productSpuPort.updateSpu(id,
                new UpdateSpuPayload(request.categoryId(), request.brandId(), request.name(), request.description(),
                        request.mainImage(), toAttrValuePayloads(request.attrValues()),
                        toSpuImagePayloads(request.images()), toSkuPayloads(request.skus())),
                operatorJwt.getTokenValue());
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('product:spu:write')")
    public void publish(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productSpuPort.publishSpu(id, operatorJwt.getTokenValue());
    }

    @PutMapping("/{id}/unlist")
    @PreAuthorize("hasAuthority('product:spu:write')")
    public void unlist(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productSpuPort.unlistSpu(id, operatorJwt.getTokenValue());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:spu:write')")
    public void delete(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        productSpuPort.deleteSpu(id, operatorJwt.getTokenValue());
    }

    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasAuthority('product:spu:write')")
    public CreateSpuResponse duplicate(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        Long newId = productSpuPort.duplicateSpu(id, operatorJwt.getTokenValue());
        return new CreateSpuResponse(newId);
    }

    private List<SpuBaseAttrValuePayload> toAttrValuePayloads(List<SpuBaseAttrValueRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SpuBaseAttrValuePayload(r.attrId(), r.attrValue())).toList();
    }

    private List<SpuImagePayload> toSpuImagePayloads(List<SpuImageRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SpuImagePayload(r.imageUrl(), r.sort())).toList();
    }

    private List<SkuPayload> toSkuPayloads(List<SkuRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream()
                .map(r -> new SkuPayload(r.name(), r.price(), r.mainImage(), r.sort(), toImagePayloads(r.images()),
                        toSaleAttrValuePayloads(r.saleAttrValues())))
                .toList();
    }

    private List<SkuImagePayload> toImagePayloads(List<SkuImageRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SkuImagePayload(r.imageUrl(), r.sort())).toList();
    }

    private List<SkuSaleAttrValuePayload> toSaleAttrValuePayloads(List<SkuSaleAttrValueRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SkuSaleAttrValuePayload(r.attrId(), r.attrValue())).toList();
    }

    private SpuDetailResponse toResponse(SpuDetailItem item) {
        var attrValues = item.attrValues().stream()
                .map(v -> new SpuBaseAttrValueResponse(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        var images = item.images().stream().map(i -> new SpuImageResponse(i.imageUrl(), i.sort())).toList();
        var skus = item.skus().stream().map(this::toSkuResponse).toList();
        return new SpuDetailResponse(item.id(), item.categoryId(), item.brandId(), item.name(), item.description(),
                item.mainImage(), item.status(), attrValues, images, skus);
    }

    private SkuDetailResponse toSkuResponse(SkuItem sku) {
        var images = sku.images().stream()
                .map(i -> new SkuImageResponse(i.imageUrl(), i.sort()))
                .toList();
        var saleAttrValues = sku.saleAttrValues().stream()
                .map(v -> new SkuSaleAttrValueResponse(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        return new SkuDetailResponse(sku.id(), sku.spuId(), sku.name(), sku.price(), sku.mainImage(),
                sku.saleCount(), sku.sort(), images, saleAttrValues);
    }
}
