package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.inventory.application.task.WareOrderTaskQueryItem;
import com.tengan.mall.inventory.application.task.WareOrderTaskQueryPort;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WareOrderTaskQueryAdapter implements WareOrderTaskQueryPort {

    private final WareOrderTaskMapper wareOrderTaskMapper;

    public WareOrderTaskQueryAdapter(WareOrderTaskMapper wareOrderTaskMapper) {
        this.wareOrderTaskMapper = wareOrderTaskMapper;
    }

    @Override
    public List<WareOrderTaskQueryItem> search(Integer status, Instant from, Instant to, int pageNum, int pageSize) {
        Page<WareOrderTaskPO> page = wareOrderTaskMapper.selectPage(new Page<>(pageNum, pageSize),
                buildWrapper(status, from, to).orderByDesc(WareOrderTaskPO::getCreatedAt)
                        .orderByDesc(WareOrderTaskPO::getId));
        return page.getRecords().stream()
                .map(po -> new WareOrderTaskQueryItem(po.getId(), po.getOrderSn(), po.getStatus().getValue(),
                        po.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()))
                .toList();
    }

    @Override
    public long countSearch(Integer status, Instant from, Instant to) {
        return wareOrderTaskMapper.selectCount(buildWrapper(status, from, to));
    }

    private LambdaQueryWrapper<WareOrderTaskPO> buildWrapper(Integer status, Instant from, Instant to) {
        LambdaQueryWrapper<WareOrderTaskPO> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(WareOrderTaskPO::getStatus, status);
        }
        if (from != null) {
            wrapper.ge(WareOrderTaskPO::getCreatedAt, from.atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (to != null) {
            wrapper.le(WareOrderTaskPO::getCreatedAt, to.atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        return wrapper;
    }
}
