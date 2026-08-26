package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuImage;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.SpuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSpuService implements CreateSpuUseCase {

    private final SpuRepository spuRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SpuCompositionAssembler assembler;
    private final ProductLaunchConfigEventPublisherPort launchConfigEventPublisher;

    public CreateSpuService(SpuRepository spuRepository, CategoryRepository categoryRepository,
            BrandRepository brandRepository, SpuCompositionAssembler assembler,
            ProductLaunchConfigEventPublisherPort launchConfigEventPublisher) {
        this.spuRepository = spuRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.assembler = assembler;
        this.launchConfigEventPublisher = launchConfigEventPublisher;
    }

    @Override
    @Transactional
    public CreateSpuResult create(CreateSpuCommand command) {
        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        if (category.getLevel() != 3) {
            throw new CategoryNotLeafException(command.categoryId());
        }
        if (!brandRepository.existsById(command.brandId())) {
            throw new BrandNotFoundException(command.brandId());
        }

        Spu spu = Spu.create(command.categoryId(), command.brandId(), command.name(), command.description(),
                command.mainImage());
        spu.scheduleLaunch(command.saleStartTime(), command.showOnLaunchTeaser(), command.teaserRemoveAt());
        spu.replaceAttrValues(assembler.resolveSpuBaseAttrValues(command.categoryId(), command.attrValues()));
        spu.replaceImages(command.images().stream().map(i -> new SpuImage(i.imageUrl(), i.sort())).toList());
        spu.replaceSkus(assembler.buildSkus(command.categoryId(), command.skus(), List.of(), null));

        Spu saved = spuRepository.save(spu);
        publishLaunchConfig(saved);
        return new CreateSpuResult(saved.getId());
    }

    private void publishLaunchConfig(Spu spu) {
        var payloads = spu.getSkus().stream()
                .map(sku -> new SkuLaunchConfigPayload(sku.getId(), spu.getSaleStartTime(),
                        sku.getPurchaseLimitPerUser()))
                .toList();
        launchConfigEventPublisher.publishUpserted(spu.getId(), payloads);
    }
}
