package com.tengan.mall.product.application.brand;

import com.tengan.mall.product.domain.repository.BrandRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class ListBrandsService implements ListBrandsUseCase {

    private final BrandRepository brandRepository;

    public ListBrandsService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public ListBrandsResult list() {
        var items = brandRepository.findAll().stream()
                .sorted(Comparator.comparingInt(brand -> brand.getSort()))
                .map(brand -> new BrandSummary(brand.getId(), brand.getName(), brand.getLogo(),
                        brand.getDescript(), brand.getFirstLetter(), brand.getSort(), brand.getStatus().getValue()))
                .toList();
        return new ListBrandsResult(items);
    }
}
