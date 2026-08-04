package com.tengan.mall.product.application.saleattr;

import com.tengan.mall.product.domain.exception.SaleAttrNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSaleAttrService implements DeleteSaleAttrUseCase {

    private final SaleAttrRepository saleAttrRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public DeleteSaleAttrService(SaleAttrRepository saleAttrRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.saleAttrRepository = saleAttrRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteSaleAttrCommand command) {
        if (!saleAttrRepository.existsById(command.id())) {
            throw new SaleAttrNotFoundException(command.id());
        }
        saleAttrRepository.deleteById(command.id());

        productOperLogRepository.save(
                ProductOperLog.create(command.operator(), "sale_attr", "delete", "刪除銷售屬性 id=" + command.id()));
    }
}
