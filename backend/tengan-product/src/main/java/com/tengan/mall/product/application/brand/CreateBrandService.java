package com.tengan.mall.product.application.brand;

import com.tengan.mall.product.domain.model.Brand;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBrandService implements CreateBrandUseCase {

    private final BrandRepository brandRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public CreateBrandService(BrandRepository brandRepository, ProductOperLogRepository productOperLogRepository) {
        this.brandRepository = brandRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public CreateBrandResult create(CreateBrandCommand command) {
        Brand brand = Brand.create(command.name(), command.logo(), command.descript(), command.firstLetter(),
                command.sort());
        Brand saved = brandRepository.save(brand);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "brand", "create",
                "新增品牌 " + saved.getName() + "（id=" + saved.getId() + "）"));

        return new CreateBrandResult(saved.getId());
    }
}
