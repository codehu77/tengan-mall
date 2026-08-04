package com.tengan.mall.admin.domain.repository;

import com.tengan.mall.admin.domain.model.OperLog;
import java.util.List;

public interface OperLogRepository {

    OperLog save(OperLog operLog);

    List<OperLog> search(OperLogSearchCriteria criteria, int pageNum, int pageSize);

    long countSearch(OperLogSearchCriteria criteria);
}
