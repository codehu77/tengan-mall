package com.tengan.mall.member.application.interest;

import com.tengan.mall.member.domain.repository.CategoryInterestRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

/**
 * 「猜你喜歡」的興趣訊號寫入端——member/[spuId]商品詳情頁瀏覽、search.vue 搜尋結果反推分類，
 * 都打這支。UNIQUE KEY(member_id, category_id, source, interest_date) 保證同一天同分類同來源
 * 只會真的寫入一次，這裡不用另外判斷「今天記過了嗎」，交給 DB 的 INSERT IGNORE 處理
 * （見 CategoryInterestRepository.recordIfAbsent 的說明）。
 */
@Service
public class RecordCategoryInterestService implements RecordCategoryInterestUseCase {

    private final CategoryInterestRepository categoryInterestRepository;

    public RecordCategoryInterestService(CategoryInterestRepository categoryInterestRepository) {
        this.categoryInterestRepository = categoryInterestRepository;
    }

    @Override
    public void record(RecordCategoryInterestCommand command) {
        categoryInterestRepository.recordIfAbsent(command.memberId(), command.categoryId(), command.source(),
                LocalDate.now());
    }
}
