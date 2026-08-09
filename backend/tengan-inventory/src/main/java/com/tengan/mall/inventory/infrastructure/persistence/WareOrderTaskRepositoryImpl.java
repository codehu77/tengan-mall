package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.inventory.domain.model.WareOrderTask;
import com.tengan.mall.inventory.domain.model.WareOrderTaskDetail;
import com.tengan.mall.inventory.domain.repository.WareOrderTaskRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class WareOrderTaskRepositoryImpl implements WareOrderTaskRepository {

    private final WareOrderTaskMapper wareOrderTaskMapper;
    private final WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    public WareOrderTaskRepositoryImpl(WareOrderTaskMapper wareOrderTaskMapper,
            WareOrderTaskDetailMapper wareOrderTaskDetailMapper) {
        this.wareOrderTaskMapper = wareOrderTaskMapper;
        this.wareOrderTaskDetailMapper = wareOrderTaskDetailMapper;
    }

    @Override
    public Optional<WareOrderTask> findByOrderSn(String orderSn) {
        WareOrderTaskPO taskPO = wareOrderTaskMapper
                .selectOne(new LambdaQueryWrapper<WareOrderTaskPO>().eq(WareOrderTaskPO::getOrderSn, orderSn));
        if (taskPO == null) {
            return Optional.empty();
        }
        List<WareOrderTaskDetail> details = wareOrderTaskDetailMapper.findByTaskId(taskPO.getId()).stream()
                .map(d -> new WareOrderTaskDetail(d.getId(), d.getWareId(), d.getSkuId(), d.getSkuCount()))
                .toList();
        return Optional.of(WareOrderTask.reconstitute(taskPO.getId(), taskPO.getOrderSn(), taskPO.getStatus(),
                details));
    }

    @Override
    public WareOrderTask save(WareOrderTask task) {
        WareOrderTaskPO taskPO = new WareOrderTaskPO();
        taskPO.setOrderSn(task.getOrderSn());
        taskPO.setStatus(task.getStatus());
        wareOrderTaskMapper.insert(taskPO);
        task.assignId(taskPO.getId());

        for (WareOrderTaskDetail detail : task.getDetails()) {
            WareOrderTaskDetailPO detailPO = new WareOrderTaskDetailPO();
            detailPO.setTaskId(taskPO.getId());
            detailPO.setWareId(detail.wareId());
            detailPO.setSkuId(detail.skuId());
            detailPO.setSkuCount(detail.skuCount());
            wareOrderTaskDetailMapper.insert(detailPO);
        }
        return task;
    }

    @Override
    public boolean markReleased(String orderSn) {
        return wareOrderTaskMapper.markReleased(orderSn) > 0;
    }

    @Override
    public boolean markDeducted(String orderSn) {
        return wareOrderTaskMapper.markDeducted(orderSn) > 0;
    }
}
