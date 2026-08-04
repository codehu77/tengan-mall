package com.tengan.mall.product.application.saleattr;

import com.tengan.mall.product.domain.exception.SaleAttrNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.model.SaleAttr;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 只改名字/是否可篩選/排序——不改 categoryId，那個定義了這個屬性的身分，改了等於是另一個屬性。 */
@Service
public class UpdateSaleAttrService implements UpdateSaleAttrUseCase {

    private final SaleAttrRepository saleAttrRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public UpdateSaleAttrService(SaleAttrRepository saleAttrRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.saleAttrRepository = saleAttrRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateSaleAttrCommand command) {
        SaleAttr attr = saleAttrRepository.findById(command.id())
                .orElseThrow(() -> new SaleAttrNotFoundException(command.id()));
        attr.rename(command.name());
        attr.updateSearchable(command.searchable());
        attr.updateSort(command.sort());
        saleAttrRepository.save(attr);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "sale_attr", "update",
                "修改銷售屬性 " + command.name() + "（id=" + command.id() + "）"));
    }
}
