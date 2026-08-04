package com.tengan.mall.admin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import com.tengan.mall.admin.domain.repository.OperLogSearchCriteria;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OperLogRepositoryImpl implements OperLogRepository {

    private final OperLogMapper operLogMapper;

    public OperLogRepositoryImpl(OperLogMapper operLogMapper) {
        this.operLogMapper = operLogMapper;
    }

    @Override
    public OperLog save(OperLog operLog) {
        OperLogPO po = toPO(operLog);
        operLogMapper.insert(po);
        operLog.assignId(po.getId());
        return operLog;
    }

    @Override
    public List<OperLog> search(OperLogSearchCriteria criteria, int pageNum, int pageSize) {
        // created_at 只到秒，短時間內連續幾筆操作很容易同一秒——只排 created_at 在分頁時不穩定
        // （同一筆在不同分頁查詢裡被排到不同名次，導致跨頁重複或漏掉），加 id 當第二排序鍵。
        Page<OperLogPO> page = operLogMapper.selectPage(new Page<>(pageNum, pageSize),
                buildWrapper(criteria).orderByDesc(OperLogPO::getCreatedAt).orderByDesc(OperLogPO::getId));
        return page.getRecords().stream().map(this::toDomain).toList();
    }

    @Override
    public long countSearch(OperLogSearchCriteria criteria) {
        return operLogMapper.selectCount(buildWrapper(criteria));
    }

    private LambdaQueryWrapper<OperLogPO> buildWrapper(OperLogSearchCriteria criteria) {
        LambdaQueryWrapper<OperLogPO> wrapper = new LambdaQueryWrapper<>();
        if (criteria.adminUserId() != null) {
            wrapper.eq(OperLogPO::getAdminUserId, criteria.adminUserId());
        }
        if (criteria.module() != null && !criteria.module().isBlank()) {
            wrapper.eq(OperLogPO::getModule, criteria.module());
        }
        if (criteria.from() != null) {
            wrapper.ge(OperLogPO::getCreatedAt, LocalDateTime.ofInstant(criteria.from(), ZoneId.systemDefault()));
        }
        if (criteria.to() != null) {
            wrapper.le(OperLogPO::getCreatedAt, LocalDateTime.ofInstant(criteria.to(), ZoneId.systemDefault()));
        }
        return wrapper;
    }

    private OperLogPO toPO(OperLog operLog) {
        OperLogPO po = new OperLogPO();
        po.setAdminUserId(operLog.getAdminUserId());
        po.setUsername(operLog.getUsername());
        po.setModule(operLog.getModule());
        po.setAction(operLog.getAction());
        po.setTargetDesc(operLog.getTargetDesc());
        po.setResultStatus(operLog.getResultStatus());
        po.setCreatedAt(LocalDateTime.ofInstant(operLog.getCreatedAt(), ZoneId.systemDefault()));
        return po;
    }

    private OperLog toDomain(OperLogPO po) {
        return OperLog.reconstitute(po.getId(), po.getAdminUserId(), po.getUsername(), po.getModule(),
                po.getAction(), po.getTargetDesc(), po.getResultStatus(),
                po.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
    }
}
