package com.tengan.mall.auth.infrastructure.persistence;

import com.tengan.mall.auth.domain.model.AccountOperLog;
import com.tengan.mall.auth.domain.repository.AccountOperLogRepository;
import java.time.ZoneId;
import org.springframework.stereotype.Repository;

@Repository
public class AccountOperLogRepositoryImpl implements AccountOperLogRepository {

    private final AccountOperLogMapper accountOperLogMapper;

    public AccountOperLogRepositoryImpl(AccountOperLogMapper accountOperLogMapper) {
        this.accountOperLogMapper = accountOperLogMapper;
    }

    @Override
    public AccountOperLog save(AccountOperLog operLog) {
        AccountOperLogPO po = new AccountOperLogPO();
        po.setOperator(operLog.getOperator());
        po.setModule(operLog.getModule());
        po.setAction(operLog.getAction());
        po.setTargetDesc(operLog.getTargetDesc());
        po.setCreatedAt(operLog.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        accountOperLogMapper.insert(po);
        operLog.assignId(po.getId());
        return operLog;
    }
}
