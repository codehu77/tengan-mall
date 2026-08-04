package com.tengan.mall.product.application.brand;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.model.Brand;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HideBrandService implements HideBrandUseCase {

    private final BrandRepository brandRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public HideBrandService(BrandRepository brandRepository, ProductOperLogRepository productOperLogRepository) {
        this.brandRepository = brandRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void hide(HideBrandCommand command) {
        Brand brand = brandRepository.findById(command.id())
                .orElseThrow(() -> new BrandNotFoundException(command.id()));
        brand.hide();
        brandRepository.save(brand);

        productOperLogRepository
                .save(ProductOperLog.create(command.operator(), "brand", "hide", "隱藏品牌 id=" + command.id()));
    }
}
