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
import java.util.Set;
import java.util.stream.Collectors;
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
    private final ProductLaunchConfigEventPublisherPort launchConfigEventPublisher;

    public UpdateSpuService(SpuRepository spuRepository, CategoryRepository categoryRepository,
            BrandRepository brandRepository, SpuCompositionAssembler assembler,
            SpuSearchDocumentAssembler searchDocumentAssembler, ProductSearchEventPublisherPort searchEventPublisher,
            ProductLaunchConfigEventPublisherPort launchConfigEventPublisher) {
        this.spuRepository = spuRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.assembler = assembler;
        this.searchDocumentAssembler = searchDocumentAssembler;
        this.searchEventPublisher = searchEventPublisher;
        this.launchConfigEventPublisher = launchConfigEventPublisher;
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

        // SKU id 穩定化：command 裡帶 id 的規格視為「編輯既有規格」，由 SpuCompositionAssembler 保留
        // 原本的 id/saleCount 走 updateById；沒帶 id 的才是真的新規格。existingSkus 要在 replaceSkus
        // 之前捕捉（replaceSkus 之後 spu.getSkus() 就是新清單了），供 assembler 對照 id 找回 saleCount，
        // 也用來在存檔後算出「這次真的被移除」的 skuId（管理員刪掉某個規格才會出現，不再是每次編輯
        // 都整批換id）——search/launch-config 的 publishRemoved 都要改吃這個精確集合，不能再用整包
        // previousSkuIds（那樣會把「其實還在、只是保留id」的規格也錯當成被移除，見規劃文件的說明）。
        List<Sku> existingSkus = spu.getSkus();
        List<Long> previousSkuIds = existingSkus.stream().map(Sku::getId).toList();
        boolean wasOnShelf = spu.getStatus() == SpuStatus.ON_SHELF;

        spu.updateBasicInfo(command.categoryId(), command.brandId(), command.name(), command.description(),
                command.mainImage());
        spu.scheduleLaunch(command.saleStartTime(), command.showOnLaunchTeaser(), command.teaserRemoveAt());
        spu.replaceAttrValues(assembler.resolveSpuBaseAttrValues(command.categoryId(), command.attrValues()));
        spu.replaceImages(command.images().stream().map(i -> new SpuImage(i.imageUrl(), i.sort())).toList());
        spu.replaceSkus(assembler.buildSkus(command.categoryId(), command.skus(), existingSkus, spu.getId()));

        spuRepository.save(spu);

        // 存檔後 spu.getSkus() 裡每顆 sku 都已經有最終 id（保留的規格是原本的 id，新規格是
        // Sku.assignId() 剛指派的），跟 previousSkuIds 取差集才是這次真正被移除的 skuId。
        Set<Long> keptIds = spu.getSkus().stream().map(Sku::getId).collect(Collectors.toSet());
        List<Long> actuallyRemovedSkuIds = previousSkuIds.stream().filter(id -> !keptIds.contains(id)).toList();

        // 只有上架中的商品才在索引裡，NEW/OFF_SHELF 狀態下改資料不需要通知 tengan-search。
        if (wasOnShelf) {
            if (!actuallyRemovedSkuIds.isEmpty()) {
                searchEventPublisher.publishRemoved(spu.getId(), actuallyRemovedSkuIds);
            }
            searchEventPublisher.publishUpserted(searchDocumentAssembler.assemble(spu));
        }

        // 跟 search 同步不同：這是內部設定同步，不管 ON_SHELF 與否都發，讓 tengan-inventory 隨時有
        // 最新的開賣時間/限購設定可用。
        if (!actuallyRemovedSkuIds.isEmpty()) {
            launchConfigEventPublisher.publishRemoved(spu.getId(), actuallyRemovedSkuIds);
        }
        var launchConfigPayloads = spu.getSkus().stream()
                .map(sku -> new SkuLaunchConfigPayload(sku.getId(), spu.getSaleStartTime(),
                        sku.getPurchaseLimitPerUser()))
                .toList();
        launchConfigEventPublisher.publishUpserted(spu.getId(), launchConfigPayloads);
    }
}
