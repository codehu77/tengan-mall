package com.tengan.mall.inventory.infrastructure.persistence;

import com.tengan.mall.inventory.domain.model.WareInfo;
import com.tengan.mall.inventory.domain.repository.WareInfoRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class WareInfoRepositoryImpl implements WareInfoRepository {

    private final WareInfoMapper wareInfoMapper;

    public WareInfoRepositoryImpl(WareInfoMapper wareInfoMapper) {
        this.wareInfoMapper = wareInfoMapper;
    }

    @Override
    public WareInfo save(WareInfo wareInfo) {
        WareInfoPO po = new WareInfoPO();
        po.setId(wareInfo.getId());
        po.setName(wareInfo.getName());
        po.setAddress(wareInfo.getAddress());
        wareInfoMapper.insert(po);
        wareInfo.assignId(po.getId());
        return wareInfo;
    }

    @Override
    public List<WareInfo> findAll() {
        return wareInfoMapper.selectList(null).stream()
                .map(po -> WareInfo.reconstitute(po.getId(), po.getName(), po.getAddress()))
                .toList();
    }
}
