package com.tengan.mall.seckill.infrastructure.persistence;

import com.tengan.mall.seckill.domain.model.SeckillSession;
import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SeckillSessionRepositoryImpl implements SeckillSessionRepository {

    private final SeckillSessionMapper mapper;

    public SeckillSessionRepositoryImpl(SeckillSessionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public SeckillSession save(SeckillSession session) {
        SeckillSessionPO po = new SeckillSessionPO();
        toPO(session, po);
        mapper.insert(po);
        session.assignId(po.getId());
        return session;
    }

    @Override
    public void update(SeckillSession session) {
        SeckillSessionPO po = new SeckillSessionPO();
        po.setId(session.getId());
        toPO(session, po);
        mapper.updateById(po);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Optional<SeckillSession> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<SeckillSession> findAll() {
        return mapper.findAllOrdered().stream().map(this::toDomain).toList();
    }

    private void toPO(SeckillSession session, SeckillSessionPO po) {
        po.setName(session.getName());
        po.setTimeOfDay(session.getTimeOfDay());
        po.setDurationMinutes(session.getDurationMinutes());
        po.setSortOrder(session.getSortOrder());
        po.setEnabled(session.isEnabled());
    }

    private SeckillSession toDomain(SeckillSessionPO po) {
        return SeckillSession.reconstitute(po.getId(), po.getName(), po.getTimeOfDay(), po.getDurationMinutes(),
                po.getSortOrder(), Boolean.TRUE.equals(po.getEnabled()));
    }
}
