package com.tengan.mall.product.application.saleattr;

import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class ListSaleAttrsService implements ListSaleAttrsUseCase {

    private final SaleAttrRepository saleAttrRepository;

    public ListSaleAttrsService(SaleAttrRepository saleAttrRepository) {
        this.saleAttrRepository = saleAttrRepository;
    }

    @Override
    public ListSaleAttrsResult list(ListSaleAttrsQuery query) {
        var items = saleAttrRepository.findByCategoryId(query.categoryId()).stream()
                .sorted(Comparator.comparingInt(a -> a.getSort()))
                .map(a -> new SaleAttrSummary(a.getId(), a.getCategoryId(), a.getName(), a.isSearchable(),
                        a.getSort()))
                .toList();
        return new ListSaleAttrsResult(items);
    }
}
