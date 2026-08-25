package com.tengan.mall.member.infrastructure.persistence;

import com.tengan.mall.member.domain.model.CategoryInterestSource;
import com.tengan.mall.member.domain.repository.CategoryAffinityRow;
import com.tengan.mall.member.domain.repository.CategoryInterestRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryInterestRepositoryImpl implements CategoryInterestRepository {

    private final CategoryInterestMapper mapper;

    public CategoryInterestRepositoryImpl(CategoryInterestMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean recordIfAbsent(Long memberId, Long categoryId, CategoryInterestSource source, LocalDate today) {
        return mapper.tryRecord(memberId, categoryId, source.getValue(), today) > 0;
    }

    @Override
    public List<CategoryAffinityRow> sumScoreByCategory(Long memberId, LocalDate since) {
        return mapper.sumScoreByCategory(memberId, since).stream()
                .map(row -> new CategoryAffinityRow(row.getCategoryId(), row.getScore()))
                .toList();
    }
}
