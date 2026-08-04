package com.tengan.mall.product.application.saleattr;

import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.model.SaleAttr;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSaleAttrService implements CreateSaleAttrUseCase {

    private final SaleAttrRepository saleAttrRepository;
    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public CreateSaleAttrService(SaleAttrRepository saleAttrRepository, CategoryRepository categoryRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.saleAttrRepository = saleAttrRepository;
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public CreateSaleAttrResult create(CreateSaleAttrCommand command) {
        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        if (category.getLevel() != 3) {
            throw new CategoryNotLeafException(command.categoryId());
        }

        SaleAttr attr = SaleAttr.create(command.categoryId(), command.name(), command.searchable(), command.sort());
        SaleAttr saved = saleAttrRepository.save(attr);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "sale_attr", "create",
                "新增銷售屬性 " + saved.getName() + "（id=" + saved.getId() + "）"));

        return new CreateSaleAttrResult(saved.getId());
    }
}
