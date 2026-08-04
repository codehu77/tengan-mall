package com.tengan.mall.product.application.baseattr;

import com.tengan.mall.product.domain.exception.BaseAttrNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteBaseAttrService implements DeleteBaseAttrUseCase {

    private final BaseAttrRepository baseAttrRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public DeleteBaseAttrService(BaseAttrRepository baseAttrRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.baseAttrRepository = baseAttrRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteBaseAttrCommand command) {
        if (!baseAttrRepository.existsById(command.id())) {
            throw new BaseAttrNotFoundException(command.id());
        }
        baseAttrRepository.deleteById(command.id());

        productOperLogRepository
                .save(ProductOperLog.create(command.operator(), "base_attr", "delete", "刪除規格參數 id=" + command.id()));
    }
}
