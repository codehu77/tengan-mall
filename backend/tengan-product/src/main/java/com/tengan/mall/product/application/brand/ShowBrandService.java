package com.tengan.mall.product.application.brand;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.model.Brand;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShowBrandService implements ShowBrandUseCase {

    private final BrandRepository brandRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public ShowBrandService(BrandRepository brandRepository, ProductOperLogRepository productOperLogRepository) {
        this.brandRepository = brandRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void show(ShowBrandCommand command) {
        Brand brand = brandRepository.findById(command.id())
                .orElseThrow(() -> new BrandNotFoundException(command.id()));
        brand.show();
        brandRepository.save(brand);

        productOperLogRepository
                .save(ProductOperLog.create(command.operator(), "brand", "show", "顯示品牌 id=" + command.id()));
    }
}
