package com.tengan.mall.product.application.baseattr.standardvalue;

import com.tengan.mall.product.domain.repository.BaseAttrStandardValueRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class ListBaseAttrStandardValuesByAttrService implements ListBaseAttrStandardValuesByAttrUseCase {

    private final BaseAttrStandardValueRepository standardValueRepository;

    public ListBaseAttrStandardValuesByAttrService(BaseAttrStandardValueRepository standardValueRepository) {
        this.standardValueRepository = standardValueRepository;
    }

    @Override
    public ListBaseAttrStandardValuesByAttrResult list(ListBaseAttrStandardValuesByAttrQuery query) {
        var items = standardValueRepository.findByAttrId(query.attrId()).stream()
                .sorted(Comparator.comparingInt(v -> v.getSort()))
                .map(v -> new BaseAttrStandardValueSummary(v.getId(), v.getAttrId(), v.getLabel(), v.isEnabled(),
                        v.getSort()))
                .toList();
        return new ListBaseAttrStandardValuesByAttrResult(items);
    }
}
