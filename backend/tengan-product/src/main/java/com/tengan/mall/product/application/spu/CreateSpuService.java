package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuImage;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSpuService implements CreateSpuUseCase {

    private final SpuRepository spuRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SpuCompositionAssembler assembler;

    public CreateSpuService(SpuRepository spuRepository, CategoryRepository categoryRepository,
            BrandRepository brandRepository, SpuCompositionAssembler assembler) {
        this.spuRepository = spuRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.assembler = assembler;
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
        spu.replaceAttrValues(assembler.resolveSpuBaseAttrValues(command.categoryId(), command.attrValues()));
        spu.replaceImages(command.images().stream().map(i -> new SpuImage(i.imageUrl(), i.sort())).toList());
        spu.replaceSkus(assembler.buildSkus(command.categoryId(), command.skus()));

        Spu saved = spuRepository.save(spu);
        return new CreateSpuResult(saved.getId());
    }
}
