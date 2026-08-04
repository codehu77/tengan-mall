package com.tengan.mall.product.application.brand;

import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteBrandService implements DeleteBrandUseCase {

    private final BrandRepository brandRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public DeleteBrandService(BrandRepository brandRepository, ProductOperLogRepository productOperLogRepository) {
        this.brandRepository = brandRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteBrandCommand command) {
        if (!brandRepository.existsById(command.id())) {
            throw new BrandNotFoundException(command.id());
        }
        brandRepository.deleteById(command.id());

        productOperLogRepository
                .save(ProductOperLog.create(command.operator(), "brand", "delete", "刪除品牌 id=" + command.id()));
    }
}
