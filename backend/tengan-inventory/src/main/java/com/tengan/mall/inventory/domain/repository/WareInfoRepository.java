package com.tengan.mall.inventory.domain.repository;

import com.tengan.mall.inventory.domain.model.WareInfo;
import java.util.List;

public interface WareInfoRepository {

    WareInfo save(WareInfo wareInfo);

    List<WareInfo> findAll();
}
