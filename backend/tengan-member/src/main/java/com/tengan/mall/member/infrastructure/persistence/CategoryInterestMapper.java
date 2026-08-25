package com.tengan.mall.member.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/** 純手寫 SQL，不用 BaseMapper/PO——這張表只有「INSERT IGNORE 寫入」跟「GROUP BY 算分數」兩種操作，
 * 沒有一般 CRUD 需求。 */
@Mapper
public interface CategoryInterestMapper {

    /** 回傳 1=第一次記錄（今天這個分類+來源還沒記過）；0=UNIQUE KEY 擋下重複，no-op。 */
    @Insert("INSERT IGNORE INTO member_category_interest (member_id, category_id, source, interest_date) "
            + "VALUES (#{memberId}, #{categoryId}, #{source}, #{date})")
    int tryRecord(@Param("memberId") Long memberId, @Param("categoryId") Long categoryId,
            @Param("source") int source, @Param("date") LocalDate date);

    /**
     * 分數 = SUM(依 source 給權重)。VIEW 權重 3、SEARCH 權重 1——直接瀏覽商品詳情頁是明確訊號，
     * 搜尋結果推回的分類是間接推論（可能是這次搜尋裡最多商品剛好落在這個分類，不代表使用者
     * 真的對「這個分類」感興趣，只是對「搜尋的東西」感興趣），所以權重壓低。
     * since 由呼叫端傳入「今天-60天」，超過這個時間窗口的興趣紀錄直接不計分（不是慢慢淡出，
     * 是整條過期規則直接排除），避免使用者很久以前一時興起的搜尋/瀏覽永久卡在分數裡。
     * member_category_interest 的 UNIQUE KEY(member_id, category_id, source, interest_date)
     * 保證同一天同分類同來源最多一列，所以這裡的 SUM 天生就是「有興趣的天數 × 權重」，不是
     * 「原始次數 × 權重」——一次爆量瀏覽 10 個商品只會貢獻 1 天份，不會被單次行為洗分數。
     */
    @Select("SELECT category_id AS categoryId, SUM(CASE WHEN source = 1 THEN 3 ELSE 1 END) AS score "
            + "FROM member_category_interest WHERE member_id = #{memberId} AND interest_date >= #{since} "
            + "GROUP BY category_id ORDER BY score DESC")
    List<CategoryAffinityScoreRow> sumScoreByCategory(@Param("memberId") Long memberId,
            @Param("since") LocalDate since);
}
