package com.tengan.mall.member.domain.repository;

import com.tengan.mall.member.domain.model.CategoryInterestSource;
import java.time.LocalDate;
import java.util.List;

public interface CategoryInterestRepository {

    /**
     * 記錄一筆「會員今天對某分類有興趣」的訊號。true=第一次記錄（今天這個分類+來源還沒記過，
     * 真的寫入一列）；false=今天已經記過了，UNIQUE KEY 擋下、no-op。這個 UNIQUE KEY 是刻意設計：
     * 同一天同分類同來源只會有一列，直接把「算興趣分數」簡化成「算列數」（見 sumScoreByCategory
     * 的說明），一次爆量瀏覽/搜尋只會貢獻 1 天份，不會被單次行為洗分數。
     */
    boolean recordIfAbsent(Long memberId, Long categoryId, CategoryInterestSource source, LocalDate today);

    /**
     * 算某會員每個分類的興趣分數（依分數高到低排序），只看 since 之後（含）的紀錄——超過這個
     * 時間窗口的興趣紀錄直接不計分，不是慢慢淡出，是整條過期規則直接排除，避免使用者很久以前
     * 一時興起的搜尋/瀏覽永久卡在分數裡。權重公式（VIEW/SEARCH 各給多少分）寫在實作的 SQL 裡，
     * 見 CategoryInterestMapper.sumScoreByCategory 的註解。
     */
    List<CategoryAffinityRow> sumScoreByCategory(Long memberId, LocalDate since);
}
