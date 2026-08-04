package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuImage;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 整批替換語意：跟 Create 一樣一次收整份 skus/attrValues，Spu.replaceSkus()/replaceAttrValues() 整批換掉。 */
@Service
public class UpdateSpuService implements UpdateSpuUseCase {

    private final SpuRepository spuRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SpuCompositionAssembler assembler;

    public UpdateSpuService(SpuRepository spuRepository, CategoryRepository categoryRepository,
            BrandRepository brandRepository, SpuCompositionAssembler assembler) {
        this.spuRepository = spuRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.assembler = assembler;
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

        spu.updateBasicInfo(command.categoryId(), command.brandId(), command.name(), command.description(),
                command.mainImage());
        spu.replaceAttrValues(assembler.resolveSpuBaseAttrValues(command.categoryId(), command.attrValues()));
        spu.replaceImages(command.images().stream().map(i -> new SpuImage(i.imageUrl(), i.sort())).toList());
        spu.replaceSkus(assembler.buildSkus(command.categoryId(), command.skus()));

        spuRepository.save(spu);
    }
}
