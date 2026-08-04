package com.tengan.mall.admin.application.operlog;

import com.tengan.mall.admin.domain.repository.OperLogRepository;
import com.tengan.mall.admin.domain.repository.OperLogSearchCriteria;
import org.springframework.stereotype.Service;

@Service
public class SearchOperLogsService implements SearchOperLogsUseCase {

    private final OperLogRepository operLogRepository;

    public SearchOperLogsService(OperLogRepository operLogRepository) {
        this.operLogRepository = operLogRepository;
    }

    @Override
    public SearchOperLogsResult search(SearchOperLogsQuery query) {
        OperLogSearchCriteria criteria = new OperLogSearchCriteria(query.adminId(), query.module(), query.from(),
                query.to());

        var items = operLogRepository.search(criteria, query.pageNum(), query.pageSize()).stream()
                .map(log -> new OperLogItem(log.getId(), log.getAdminUserId(), log.getUsername(), log.getModule(),
                        log.getAction(), log.getTargetDesc(), log.getResultStatus(), log.getCreatedAt()))
                .toList();

        return new SearchOperLogsResult(items, operLogRepository.countSearch(criteria));
    }
}
