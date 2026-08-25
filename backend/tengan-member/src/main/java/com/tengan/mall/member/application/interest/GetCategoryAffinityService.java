package com.tengan.mall.member.application.interest;

import com.tengan.mall.member.domain.repository.CategoryInterestRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 「猜你喜歡」的分類分數排名——首頁無限捲動用這支決定走訪順序（見前端 useGuessYouLike）。
 * 60 天窗口是這次的 demo 用途經驗值（比照 CountLowStockService.LOW_STOCK_THRESHOLD 那次的判斷，
 * 不用另外加 Nacos 設定項）：太短會讓分數變化太劇烈（幾天沒登入分數就歸零），太長又會讓很久以前
 * 一時興起的搜尋/瀏覽卡在分數裡太久，60 天大約是「一到兩個月的購物週期」的量級，抓個折衷值。
 * 真正的權重公式（VIEW/SEARCH 各給多少分）算在 CategoryInterestMapper.sumScoreByCategory 裡，
 * 這裡只負責決定「看多久以內的紀錄」。
 */
@Service
public class GetCategoryAffinityService implements GetCategoryAffinityUseCase {

    private static final int LOOKBACK_DAYS = 60;

    private final CategoryInterestRepository categoryInterestRepository;

    public GetCategoryAffinityService(CategoryInterestRepository categoryInterestRepository) {
        this.categoryInterestRepository = categoryInterestRepository;
    }

    @Override
    public List<CategoryAffinityItem> get(Long memberId) {
        LocalDate since = LocalDate.now().minusDays(LOOKBACK_DAYS);
        return categoryInterestRepository.sumScoreByCategory(memberId, since).stream()
                .map(row -> new CategoryAffinityItem(row.categoryId(), row.score()))
                .toList();
    }
}
