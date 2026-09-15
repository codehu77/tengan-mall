package com.tengan.mall.product.application.saleattr.standardvalue;

import com.tengan.mall.product.domain.repository.SaleAttrStandardValueRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class ListSaleAttrStandardValuesByAttrService implements ListSaleAttrStandardValuesByAttrUseCase {

    private final SaleAttrStandardValueRepository standardValueRepository;

    public ListSaleAttrStandardValuesByAttrService(SaleAttrStandardValueRepository standardValueRepository) {
        this.standardValueRepository = standardValueRepository;
    }

    @Override
    public ListSaleAttrStandardValuesByAttrResult list(ListSaleAttrStandardValuesByAttrQuery query) {
        var items = standardValueRepository.findByAttrId(query.attrId()).stream()
                .sorted(Comparator.comparingInt(v -> v.getSort()))
                .map(v -> new SaleAttrStandardValueSummary(v.getId(), v.getAttrId(), v.getLabel(), v.isEnabled(),
                        v.getSort()))
                .toList();
        return new ListSaleAttrStandardValuesByAttrResult(items);
    }
}
