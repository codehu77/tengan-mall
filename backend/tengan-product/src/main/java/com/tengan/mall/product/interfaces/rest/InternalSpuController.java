package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.product.application.spu.BatchGetSpuSummariesUseCase;
import com.tengan.mall.product.application.spu.CreateSpuCommand;
import com.tengan.mall.product.application.spu.CreateSpuUseCase;
import com.tengan.mall.product.application.spu.DeleteSpuCommand;
import com.tengan.mall.product.application.spu.DeleteSpuUseCase;
import com.tengan.mall.product.application.spu.DuplicateSpuUseCase;
import com.tengan.mall.product.application.spu.GetSpuDetailResult;
import com.tengan.mall.product.application.spu.GetSpuDetailUseCase;
import com.tengan.mall.product.application.spu.ListSpusUseCase;
import com.tengan.mall.product.application.spu.PublishSpuCommand;
import com.tengan.mall.product.application.spu.PublishSpuUseCase;
import com.tengan.mall.product.application.spu.SearchSpusQuery;
import com.tengan.mall.product.application.spu.SkuCommand;
import com.tengan.mall.product.application.spu.SkuImageCommand;
import com.tengan.mall.product.application.spu.SkuSaleAttrValueCommand;
import com.tengan.mall.product.application.spu.SpuBaseAttrValueCommand;
import com.tengan.mall.product.application.spu.SpuImageCommand;
import com.tengan.mall.product.application.spu.UnlistSpuCommand;
import com.tengan.mall.product.application.spu.UnlistSpuUseCase;
import com.tengan.mall.product.application.spu.UpdateSpuCommand;
import com.tengan.mall.product.application.spu.UpdateSpuUseCase;
import com.tengan.mall.product.interfaces.rest.dto.CreateSpuRequest;
import com.tengan.mall.product.interfaces.rest.dto.CreateSpuResponse;
import com.tengan.mall.product.interfaces.rest.dto.ListSpusResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuDetailResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuImageRequest;
import com.tengan.mall.product.interfaces.rest.dto.SkuImageResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuRequest;
import com.tengan.mall.product.interfaces.rest.dto.SkuSaleAttrValueRequest;
import com.tengan.mall.product.interfaces.rest.dto.SkuSaleAttrValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuBaseAttrValueRequest;
import com.tengan.mall.product.interfaces.rest.dto.SpuBaseAttrValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuBatchSummaryResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuDetailResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuImageRequest;
import com.tengan.mall.product.interfaces.rest.dto.SpuImageResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuSummaryResponse;
import com.tengan.mall.product.interfaces.rest.dto.UpdateSpuRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products/spus")
public class InternalSpuController {

    private final ListSpusUseCase listSpusUseCase;
    private final GetSpuDetailUseCase getSpuDetailUseCase;
    private final CreateSpuUseCase createSpuUseCase;
    private final UpdateSpuUseCase updateSpuUseCase;
    private final PublishSpuUseCase publishSpuUseCase;
    private final UnlistSpuUseCase unlistSpuUseCase;
    private final DeleteSpuUseCase deleteSpuUseCase;
    private final DuplicateSpuUseCase duplicateSpuUseCase;
    private final BatchGetSpuSummariesUseCase batchGetSpuSummariesUseCase;

    public InternalSpuController(ListSpusUseCase listSpusUseCase, GetSpuDetailUseCase getSpuDetailUseCase,
            CreateSpuUseCase createSpuUseCase, UpdateSpuUseCase updateSpuUseCase,
            PublishSpuUseCase publishSpuUseCase, UnlistSpuUseCase unlistSpuUseCase,
            DeleteSpuUseCase deleteSpuUseCase, DuplicateSpuUseCase duplicateSpuUseCase,
            BatchGetSpuSummariesUseCase batchGetSpuSummariesUseCase) {
        this.listSpusUseCase = listSpusUseCase;
        this.getSpuDetailUseCase = getSpuDetailUseCase;
        this.createSpuUseCase = createSpuUseCase;
        this.updateSpuUseCase = updateSpuUseCase;
        this.publishSpuUseCase = publishSpuUseCase;
        this.unlistSpuUseCase = unlistSpuUseCase;
        this.deleteSpuUseCase = deleteSpuUseCase;
        this.duplicateSpuUseCase = duplicateSpuUseCase;
        this.batchGetSpuSummariesUseCase = batchGetSpuSummariesUseCase;
    }

    /** 供 tengan-seckill 展示端點分組用（只要 name/mainImage，見 BatchGetSpuSummariesUseCase 說明）。 */
    @GetMapping("/batch")
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public List<SpuBatchSummaryResponse> batchGet(@RequestParam List<Long> ids) {
        return batchGetSpuSummariesUseCase.get(ids).stream()
                .map(s -> new SpuBatchSummaryResponse(s.id(), s.name(), s.mainImage()))
                .toList();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public ListSpusResponse list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        var result = listSpusUseCase
                .search(new SearchSpusQuery(categoryId, brandId, name, status, pageNum, pageSize));
        var items = result.items().stream()
                .map(s -> new SpuSummaryResponse(s.id(), s.categoryId(), s.brandId(), s.name(), s.mainImage(),
                        s.status(), s.skuCount()))
                .toList();
        return new ListSpusResponse(items, result.total());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.read')")
    public SpuDetailResponse get(@PathVariable Long id) {
        return toResponse(getSpuDetailUseCase.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateSpuResponse> create(@Valid @RequestBody CreateSpuRequest request) {
        var result = createSpuUseCase.create(new CreateSpuCommand(request.categoryId(), request.brandId(),
                request.name(), request.description(), request.mainImage(), toAttrValueCommands(request.attrValues()),
                toSpuImageCommands(request.images()), toSkuCommands(request.skus())));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateSpuResponse(result.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateSpuRequest request) {
        updateSpuUseCase.update(new UpdateSpuCommand(id, request.categoryId(), request.brandId(), request.name(),
                request.description(), request.mainImage(), toAttrValueCommands(request.attrValues()),
                toSpuImageCommands(request.images()), toSkuCommands(request.skus())));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> publish(@PathVariable Long id) {
        publishSpuUseCase.publish(new PublishSpuCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unlist")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> unlist(@PathVariable Long id) {
        unlistSpuUseCase.unlist(new UnlistSpuCommand(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteSpuUseCase.delete(new DeleteSpuCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasAuthority('SCOPE_product.write')")
    public ResponseEntity<CreateSpuResponse> duplicate(@PathVariable Long id) {
        var result = duplicateSpuUseCase.duplicate(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateSpuResponse(result.id()));
    }

    private List<SpuBaseAttrValueCommand> toAttrValueCommands(List<SpuBaseAttrValueRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SpuBaseAttrValueCommand(r.attrId(), r.attrValue())).toList();
    }

    private List<SpuImageCommand> toSpuImageCommands(List<SpuImageRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SpuImageCommand(r.imageUrl(), r.sort())).toList();
    }

    private List<SkuCommand> toSkuCommands(List<SkuRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream()
                .map(r -> new SkuCommand(r.name(), r.price(), r.mainImage(), r.sort(), toImageCommands(r.images()),
                        toSaleAttrValueCommands(r.saleAttrValues())))
                .toList();
    }

    private List<SkuImageCommand> toImageCommands(List<SkuImageRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SkuImageCommand(r.imageUrl(), r.sort())).toList();
    }

    private List<SkuSaleAttrValueCommand> toSaleAttrValueCommands(List<SkuSaleAttrValueRequest> requests) {
        return requests == null ? List.of()
                : requests.stream().map(r -> new SkuSaleAttrValueCommand(r.attrId(), r.attrValue())).toList();
    }

    private SpuDetailResponse toResponse(GetSpuDetailResult result) {
        var attrValues = result.attrValues().stream()
                .map(v -> new SpuBaseAttrValueResponse(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        var images = result.images().stream()
                .map(i -> new SpuImageResponse(i.imageUrl(), i.sort()))
                .toList();
        var skus = result.skus().stream()
                .map(s -> new SkuDetailResponse(s.id(), s.spuId(), s.name(), s.price(), s.mainImage(),
                        s.saleCount(), s.sort(),
                        s.images().stream().map(i -> new SkuImageResponse(i.imageUrl(), i.sort()))
                                .toList(),
                        s.saleAttrValues().stream()
                                .map(v -> new SkuSaleAttrValueResponse(v.attrId(), v.attrName(), v.attrValue()))
                                .toList()))
                .toList();
        return new SpuDetailResponse(result.id(), result.categoryId(), result.brandId(), result.name(),
                result.description(), result.mainImage(), result.status(), attrValues, images, skus);
    }
}
