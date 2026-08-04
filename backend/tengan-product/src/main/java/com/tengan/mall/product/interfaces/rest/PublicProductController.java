package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.product.application.category.CategoryTreeNode;
import com.tengan.mall.product.application.category.GetCategoryTreeUseCase;
import com.tengan.mall.product.application.spu.GetPublicSkuDetailUseCase;
import com.tengan.mall.product.application.spu.GetPublicSpuDetailUseCase;
import com.tengan.mall.product.application.spu.SkuDetailView;
import com.tengan.mall.product.interfaces.rest.dto.CategoryTreeNodeResponse;
import com.tengan.mall.product.interfaces.rest.dto.CategoryTreeResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuDetailResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuImageResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuSaleAttrValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuBaseAttrValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuDetailResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/products")
public class PublicProductController {

    private final GetCategoryTreeUseCase getCategoryTreeUseCase;
    private final GetPublicSpuDetailUseCase getPublicSpuDetailUseCase;
    private final GetPublicSkuDetailUseCase getPublicSkuDetailUseCase;

    public PublicProductController(GetCategoryTreeUseCase getCategoryTreeUseCase,
            GetPublicSpuDetailUseCase getPublicSpuDetailUseCase,
            GetPublicSkuDetailUseCase getPublicSkuDetailUseCase) {
        this.getCategoryTreeUseCase = getCategoryTreeUseCase;
        this.getPublicSpuDetailUseCase = getPublicSpuDetailUseCase;
        this.getPublicSkuDetailUseCase = getPublicSkuDetailUseCase;
    }

    @GetMapping("/categories/tree")
    public CategoryTreeResponse categoriesTree() {
        var items = getCategoryTreeUseCase.tree().items().stream().map(this::toResponse).toList();
        return new CategoryTreeResponse(items);
    }

    @GetMapping("/spus/{id}")
    public SpuDetailResponse spuDetail(@PathVariable Long id) {
        var result = getPublicSpuDetailUseCase.get(id);
        var attrValues = result.attrValues().stream()
                .map(v -> new SpuBaseAttrValueResponse(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        var images = result.images().stream().map(i -> new SpuImageResponse(i.imageUrl(), i.sort())).toList();
        var skus = result.skus().stream().map(this::toResponse).toList();
        return new SpuDetailResponse(result.id(), result.categoryId(), result.brandId(), result.name(),
                result.description(), result.mainImage(), result.status(), attrValues, images, skus);
    }

    @GetMapping("/skus/{id}")
    public SkuDetailResponse skuDetail(@PathVariable Long id) {
        return toResponse(getPublicSkuDetailUseCase.get(id));
    }

    private SkuDetailResponse toResponse(SkuDetailView view) {
        var images = view.images().stream().map(i -> new SkuImageResponse(i.imageUrl(), i.sort()))
                .toList();
        var saleAttrValues = view.saleAttrValues().stream()
                .map(v -> new SkuSaleAttrValueResponse(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        return new SkuDetailResponse(view.id(), view.spuId(), view.name(), view.price(), view.mainImage(),
                view.saleCount(), view.sort(), images, saleAttrValues);
    }

    private CategoryTreeNodeResponse toResponse(CategoryTreeNode node) {
        var children = node.children().stream().map(this::toResponse).toList();
        return new CategoryTreeNodeResponse(node.id(), node.name(), node.icon(), node.sort(), node.status(),
                children);
    }
}
