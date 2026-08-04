package com.tengan.mall.product.application.brand;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.model.Brand;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateBrandService implements UpdateBrandUseCase {

    private final BrandRepository brandRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public UpdateBrandService(BrandRepository brandRepository, ProductOperLogRepository productOperLogRepository) {
        this.brandRepository = brandRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateBrandCommand command) {
        Brand brand = brandRepository.findById(command.id())
                .orElseThrow(() -> new BrandNotFoundException(command.id()));
        brand.rename(command.name());
        brand.updateLogo(command.logo());
        brand.updateDescript(command.descript());
        brand.updateFirstLetter(command.firstLetter());
        brand.updateSort(command.sort());
        brandRepository.save(brand);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "brand", "update",
                "修改品牌 " + command.name() + "（id=" + command.id() + "）"));
    }
}
