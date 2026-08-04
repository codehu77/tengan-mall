package com.tengan.mall.product.application.baseattr;

import com.tengan.mall.product.domain.exception.BaseAttrGroupCategoryMismatchException;
import com.tengan.mall.product.domain.exception.BaseAttrGroupNotFoundException;
import com.tengan.mall.product.domain.exception.BaseAttrNotFoundException;
import com.tengan.mall.product.domain.model.BaseAttr;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 改名字/是否可篩選/排序，也可以改掛到同分類底下的另一個 attrGroupId（後台拖曳跨分組即走這條路徑）——
 * 不改 categoryId，那個定義了這個屬性歸屬哪個分類，改了等於是另一個屬性。
 */
@Service
public class UpdateBaseAttrService implements UpdateBaseAttrUseCase {

    private final BaseAttrRepository baseAttrRepository;
    private final BaseAttrGroupRepository baseAttrGroupRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public UpdateBaseAttrService(BaseAttrRepository baseAttrRepository,
            BaseAttrGroupRepository baseAttrGroupRepository, ProductOperLogRepository productOperLogRepository) {
        this.baseAttrRepository = baseAttrRepository;
        this.baseAttrGroupRepository = baseAttrGroupRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateBaseAttrCommand command) {
        BaseAttr attr = baseAttrRepository.findById(command.id())
                .orElseThrow(() -> new BaseAttrNotFoundException(command.id()));

        if (!command.attrGroupId().equals(attr.getAttrGroupId())) {
            var attrGroup = baseAttrGroupRepository.findById(command.attrGroupId())
                    .orElseThrow(() -> new BaseAttrGroupNotFoundException(command.attrGroupId()));
            if (!attrGroup.getCategoryId().equals(attr.getCategoryId())) {
                throw new BaseAttrGroupCategoryMismatchException(command.attrGroupId(), attr.getCategoryId());
            }
            attr.moveToAttrGroup(command.attrGroupId());
        }
        attr.rename(command.name());
        attr.updateSearchable(command.searchable());
        attr.updateSort(command.sort());
        baseAttrRepository.save(attr);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "base_attr", "update",
                "修改規格參數 " + command.name() + "（id=" + command.id() + "）"));
    }
}
