package com.tengan.mall.inventory.application.warehouse;

import com.tengan.mall.inventory.domain.repository.WareInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class ListWarehousesService implements ListWarehousesUseCase {

    private final WareInfoRepository wareInfoRepository;

    public ListWarehousesService(WareInfoRepository wareInfoRepository) {
        this.wareInfoRepository = wareInfoRepository;
    }

    @Override
    public ListWarehousesResult list() {
        var items = wareInfoRepository.findAll().stream()
                .map(w -> new WarehouseSummary(w.getId(), w.getName(), w.getAddress()))
                .toList();
        return new ListWarehousesResult(items);
    }
}
