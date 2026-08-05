package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuImage;
import com.tengan.mall.product.domain.model.SpuStatus;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.SpuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 整批替換語意：跟 Create 一樣一次收整份 skus/attrValues，Spu.replaceSkus()/replaceAttrValues() 整批換掉。 */
@Service
public class UpdateSpuService implements UpdateSpuUseCase {

    private final SpuRepository spuRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SpuCompositionAssembler assembler;
    private final SpuSearchDocumentAssembler searchDocumentAssembler;
    private final ProductSearchEventPublisherPort searchEventPublisher;

    public UpdateSpuService(SpuRepository spuRepository, CategoryRepository categoryRepository,
            BrandRepository brandRepository, SpuCompositionAssembler assembler,
            SpuSearchDocumentAssembler searchDocumentAssembler, ProductSearchEventPublisherPort searchEventPublisher) {
        this.spuRepository = spuRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.assembler = assembler;
        this.searchDocumentAssembler = searchDocumentAssembler;
        this.searchEventPublisher = searchEventPublisher;
    }

    @Override
    @Transactional
    public void update(UpdateSpuCommand command) {
        Spu spu = spuRepository.findById(command.spuId())
                .orElseThrow(() -> new SpuNotFoundException(command.spuId()));

        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        if (category.getLevel() != 3) {
            throw new CategoryNotLeafException(command.categoryId());
        }
        if (!brandRepository.existsById(command.brandId())) {
            throw new BrandNotFoundException(command.brandId());
        }

        // 更新是「整批替換」語意：SpuRepositoryImpl.saveSkus() 對 Sku 集合一律視為全新插入
        // （command 沒有帶舊 skuId，Sku.create() 出來的 id 永遠是 null），所以舊的 sku 列會被
        // 整批刪除、換上全新 id 的新列——即使只是改個名稱/價格，skuId 也會整個換掉。這代表舊
        // sku 對應的搜尋文件也要整批移除，不能只發 upserted 事件，不然舊文件會永遠留在 ES 裡
        // （改容量規格文字後，搜尋結果同時出現改之前跟改之後兩筆，就是這個坑）。
        List<Long> previousSkuIds = spu.getSkus().stream().map(Sku::getId).toList();
        boolean wasOnShelf = spu.getStatus() == SpuStatus.ON_SHELF;

        spu.updateBasicInfo(command.categoryId(), command.brandId(), command.name(), command.description(),
                command.mainImage());
        spu.replaceAttrValues(assembler.resolveSpuBaseAttrValues(command.categoryId(), command.attrValues()));
        spu.replaceImages(command.images().stream().map(i -> new SpuImage(i.imageUrl(), i.sort())).toList());
        spu.replaceSkus(assembler.buildSkus(command.categoryId(), command.skus()));

        spuRepository.save(spu);

        // 只有上架中的商品才在索引裡，NEW/OFF_SHELF 狀態下改資料不需要通知 tengan-search。
        if (wasOnShelf) {
            if (!previousSkuIds.isEmpty()) {
                searchEventPublisher.publishRemoved(spu.getId(), previousSkuIds);
            }
            searchEventPublisher.publishUpserted(searchDocumentAssembler.assemble(spu));
        }
    }
}
