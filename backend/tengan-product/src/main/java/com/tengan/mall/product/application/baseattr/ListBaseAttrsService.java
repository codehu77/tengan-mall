package com.tengan.mall.product.application.baseattr;

import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class ListBaseAttrsService implements ListBaseAttrsUseCase {

    private final BaseAttrRepository baseAttrRepository;

    public ListBaseAttrsService(BaseAttrRepository baseAttrRepository) {
        this.baseAttrRepository = baseAttrRepository;
    }

    @Override
    public ListBaseAttrsResult list(ListBaseAttrsQuery query) {
        var items = baseAttrRepository.findByCategoryId(query.categoryId()).stream()
                .sorted(Comparator.comparingInt(a -> a.getSort()))
                .map(a -> new BaseAttrSummary(a.getId(), a.getCategoryId(), a.getAttrGroupId(), a.getName(),
                        a.isSearchable(), a.getSort()))
                .toList();
        return new ListBaseAttrsResult(items);
    }
}
