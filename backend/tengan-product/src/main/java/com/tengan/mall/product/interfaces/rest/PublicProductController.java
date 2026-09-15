package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.product.application.category.CategoryTreeNode;
import com.tengan.mall.product.application.category.GetCategoryTreeUseCase;
import com.tengan.mall.product.application.spu.GetPublicSkuDetailUseCase;
import com.tengan.mall.product.application.spu.GetPublicSpuDetailUseCase;
import com.tengan.mall.product.application.spu.LaunchTeaserSpuView;
import com.tengan.mall.product.application.spu.ListLaunchTeaserSpusUseCase;
import com.tengan.mall.product.application.spu.SkuDetailView;
import com.tengan.mall.product.interfaces.rest.dto.CategoryTreeNodeResponse;
import com.tengan.mall.product.interfaces.rest.dto.CategoryTreeResponse;
import com.tengan.mall.product.interfaces.rest.dto.LaunchTeaserListResponse;
import com.tengan.mall.product.interfaces.rest.dto.LaunchTeaserPageResponse;
import com.tengan.mall.product.interfaces.rest.dto.LaunchTeaserSpuResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuDetailResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuImageResponse;
import com.tengan.mall.product.interfaces.rest.dto.SkuSaleAttrValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuBaseAttrValueResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuDetailResponse;
import com.tengan.mall.product.interfaces.rest.dto.SpuImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/products")
public class PublicProductController {

    private final GetCategoryTreeUseCase getCategoryTreeUseCase;
    private final GetPublicSpuDetailUseCase getPublicSpuDetailUseCase;
    private final GetPublicSkuDetailUseCase getPublicSkuDetailUseCase;
    private final ListLaunchTeaserSpusUseCase listLaunchTeaserSpusUseCase;

    public PublicProductController(GetCategoryTreeUseCase getCategoryTreeUseCase,
            GetPublicSpuDetailUseCase getPublicSpuDetailUseCase,
            GetPublicSkuDetailUseCase getPublicSkuDetailUseCase,
            ListLaunchTeaserSpusUseCase listLaunchTeaserSpusUseCase) {
        this.getCategoryTreeUseCase = getCategoryTreeUseCase;
        this.getPublicSpuDetailUseCase = getPublicSpuDetailUseCase;
        this.getPublicSkuDetailUseCase = getPublicSkuDetailUseCase;
        this.listLaunchTeaserSpusUseCase = listLaunchTeaserSpusUseCase;
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
                .map(v -> new SpuBaseAttrValueResponse(v.attrId(), v.attrName(), v.attrValue(), v.standardValueId(),
                        v.unit()))
                .toList();
        var images = result.images().stream().map(i -> new SpuImageResponse(i.imageUrl(), i.sort())).toList();
        var skus = result.skus().stream().map(this::toResponse).toList();
        return new SpuDetailResponse(result.id(), result.categoryId(), result.catalog1Id(), result.brandId(),
                result.name(), result.description(), result.mainImage(), result.status(), result.saleStartTime(),
                result.showOnLaunchTeaser(), result.teaserRemoveAt(), attrValues, images, skus);
    }

    @GetMapping("/skus/{id}")
    public SkuDetailResponse skuDetail(@PathVariable Long id) {
        return toResponse(getPublicSkuDetailUseCase.get(id));
    }

    /** 首頁「即將開賣」預告用：不分頁，cap 在 limit 筆內，前端自己 shuffle+裁切成兩列。 */
    @GetMapping("/launch-teaser")
    public LaunchTeaserListResponse launchTeaser(@RequestParam(defaultValue = "20") int limit) {
        var items = listLaunchTeaserSpusUseCase.listForHome(limit).stream().map(this::toResponse).toList();
        return new LaunchTeaserListResponse(items);
    }

    /** 「看更多」整頁用：分頁版本。 */
    @GetMapping("/launch-teaser/page")
    public LaunchTeaserPageResponse launchTeaserPage(@RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = listLaunchTeaserSpusUseCase.search(pageNum, pageSize);
        var items = result.items().stream().map(this::toResponse).toList();
        return new LaunchTeaserPageResponse(items, result.total());
    }

    private LaunchTeaserSpuResponse toResponse(LaunchTeaserSpuView view) {
        return new LaunchTeaserSpuResponse(view.id(), view.name(), view.mainImage(), view.price(),
                view.saleStartTime());
    }

    private SkuDetailResponse toResponse(SkuDetailView view) {
        var images = view.images().stream().map(i -> new SkuImageResponse(i.imageUrl(), i.sort()))
                .toList();
        var saleAttrValues = view.saleAttrValues().stream()
                .map(v -> new SkuSaleAttrValueResponse(v.attrId(), v.attrName(), v.attrValue(), v.standardValueId(),
                        v.unit()))
                .toList();
        return new SkuDetailResponse(view.id(), view.spuId(), view.name(), view.price(), view.mainImage(),
                view.saleCount(), view.sort(), view.purchaseLimitPerUser(), images, saleAttrValues);
    }

    private CategoryTreeNodeResponse toResponse(CategoryTreeNode node) {
        var children = node.children().stream().map(this::toResponse).toList();
        return new CategoryTreeNodeResponse(node.id(), node.name(), node.icon(), node.sort(), node.status(),
                children);
    }
}
