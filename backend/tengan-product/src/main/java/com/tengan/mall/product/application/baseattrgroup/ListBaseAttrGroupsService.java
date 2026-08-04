package com.tengan.mall.product.application.baseattrgroup;

import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class ListBaseAttrGroupsService implements ListBaseAttrGroupsUseCase {

    private final BaseAttrGroupRepository baseAttrGroupRepository;

    public ListBaseAttrGroupsService(BaseAttrGroupRepository baseAttrGroupRepository) {
        this.baseAttrGroupRepository = baseAttrGroupRepository;
    }

    @Override
    public ListBaseAttrGroupsResult list(ListBaseAttrGroupsQuery query) {
        var items = baseAttrGroupRepository.findByCategoryId(query.categoryId()).stream()
                .sorted(Comparator.comparingInt(g -> g.getSort()))
                .map(g -> new BaseAttrGroupSummary(g.getId(), g.getCategoryId(), g.getName(), g.getSort()))
                .toList();
        return new ListBaseAttrGroupsResult(items);
    }
}
