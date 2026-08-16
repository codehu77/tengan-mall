package com.tengan.mall.wallet.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.wallet.domain.model.PointsTransaction;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import com.tengan.mall.wallet.domain.repository.TypeStatusCount;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PointsTransactionRepositoryImpl implements PointsTransactionRepository {

    private final PointsTransactionMapper mapper;

    public PointsTransactionRepositoryImpl(PointsTransactionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PointsTransaction save(PointsTransaction transaction) {
        PointsTransactionPO po = new PointsTransactionPO();
        po.setMemberId(transaction.getMemberId());
        po.setType(transaction.getType());
        po.setStatus(transaction.getStatus());
        po.setPoints(transaction.getPoints());
        po.setBalanceAfter(transaction.getBalanceAfter());
        po.setTitle(transaction.getTitle());
        po.setDescription(transaction.getDescription());
        po.setOrderSn(transaction.getOrderSn());
        po.setChannel(transaction.getChannel());
        po.setOperator(transaction.getOperator());
        po.setCreatedAt(toLocalDateTime(transaction.getCreatedAt()));
        po.setExpiresAt(toLocalDateTime(transaction.getExpiresAt()));
        po.setRelatedTransactionId(transaction.getRelatedTransactionId());
        mapper.insert(po);
        transaction.assignId(po.getId());
        return transaction;
    }

    @Override
    public Optional<PointsTransaction> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<PointsTransaction> findEarnByOrderSn(String orderSn) {
        return Optional
                .ofNullable(mapper.selectOne(new LambdaQueryWrapper<PointsTransactionPO>()
                        .eq(PointsTransactionPO::getOrderSn, orderSn).eq(PointsTransactionPO::getType, 1)))
                .map(this::toDomain);
    }

    @Override
    public Optional<PointsTransaction> findPendingEarnByOrderSn(String orderSn) {
        return Optional
                .ofNullable(mapper.selectOne(new LambdaQueryWrapper<PointsTransactionPO>()
                        .eq(PointsTransactionPO::getOrderSn, orderSn).eq(PointsTransactionPO::getType, 1)
                        .eq(PointsTransactionPO::getStatus, 1)))
                .map(this::toDomain);
    }

    @Override
    public boolean confirmPending(String orderSn, int finalPoints, int balanceAfter, Instant expiresAt, String title,
            String description) {
        return mapper.confirmPending(orderSn, finalPoints, balanceAfter, toLocalDateTime(expiresAt), title,
                description) > 0;
    }

    @Override
    public Optional<PointsTransaction> findConfirmedRedeemByOrderSn(Long memberId, String orderSn) {
        return Optional
                .ofNullable(mapper.selectOne(new LambdaQueryWrapper<PointsTransactionPO>()
                        .eq(PointsTransactionPO::getMemberId, memberId).eq(PointsTransactionPO::getOrderSn, orderSn)
                        .eq(PointsTransactionPO::getType, 2).eq(PointsTransactionPO::getStatus, 2)))
                .map(this::toDomain);
    }

    @Override
    public boolean revertRedeem(Long id, String orderSn) {
        return mapper.revertRedeem(id, orderSn) > 0;
    }

    @Override
    public int sumConfirmedPoints(Long memberId) {
        return mapper.sumConfirmedPoints(memberId);
    }

    @Override
    public int sumPendingPoints(Long memberId) {
        return mapper.sumPendingPoints(memberId);
    }

    @Override
    public int sumConfirmedEarnPointsSince(Long memberId, Instant monthStart) {
        return mapper.sumConfirmedEarnPointsSince(memberId, toLocalDateTime(monthStart));
    }

    @Override
    public List<PointsTransaction> findActiveEarnBatches(Long memberId) {
        return mapper.findActiveEarnBatches(memberId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PointsTransaction> findExpirableEarnBatches(Instant now, int limit) {
        return mapper.findExpirableEarnBatches(toLocalDateTime(now), limit).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PointsTransaction> search(Long memberId, Integer type, Integer status, Instant fromDate,
            String keyword, int pageNum, int pageSize) {
        Page<PointsTransactionPO> page = mapper.selectPage(new Page<>(pageNum, pageSize),
                buildWrapper(memberId, type, status, fromDate, keyword).orderByDesc(PointsTransactionPO::getCreatedAt)
                        .orderByDesc(PointsTransactionPO::getId));
        return page.getRecords().stream().map(this::toDomain).toList();
    }

    @Override
    public long countSearch(Long memberId, Integer type, Integer status, Instant fromDate, String keyword) {
        return mapper.selectCount(buildWrapper(memberId, type, status, fromDate, keyword));
    }

    @Override
    public List<TypeStatusCount> countGroupedByTypeAndStatus(Long memberId) {
        return mapper.countGroupedByTypeAndStatus(memberId).stream()
                .map(row -> new TypeStatusCount(row.getType(), row.getStatus(), row.getCnt()))
                .toList();
    }

    private LambdaQueryWrapper<PointsTransactionPO> buildWrapper(Long memberId, Integer type, Integer status,
            Instant fromDate, String keyword) {
        LambdaQueryWrapper<PointsTransactionPO> wrapper = new LambdaQueryWrapper<PointsTransactionPO>()
                .eq(PointsTransactionPO::getMemberId, memberId);
        if (type != null) {
            wrapper.eq(PointsTransactionPO::getType, type);
        }
        if (status != null) {
            wrapper.eq(PointsTransactionPO::getStatus, status);
        }
        if (fromDate != null) {
            wrapper.ge(PointsTransactionPO::getCreatedAt, toLocalDateTime(fromDate));
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(PointsTransactionPO::getTitle, keyword)
                    .or().like(PointsTransactionPO::getDescription, keyword));
        }
        return wrapper;
    }

    private PointsTransaction toDomain(PointsTransactionPO po) {
        return PointsTransaction.reconstitute(po.getId(), po.getMemberId(), po.getType(), po.getStatus(),
                po.getPoints(), po.getBalanceAfter(), po.getTitle(), po.getDescription(), po.getOrderSn(),
                po.getChannel(), po.getOperator(), toInstant(po.getCreatedAt()), toInstant(po.getExpiresAt()),
                po.getRelatedTransactionId());
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
