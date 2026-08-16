package com.tengan.mall.wallet.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PointsTransactionMapper extends BaseMapper<PointsTransactionPO> {

    @Update("UPDATE points_transaction SET status = 2, points = #{finalPoints}, balance_after = #{balanceAfter}, "
            + "expires_at = #{expiresAt}, title = #{title}, description = #{description} "
            + "WHERE order_sn = #{orderSn} AND type = 1 AND status = 1")
    int confirmPending(@Param("orderSn") String orderSn, @Param("finalPoints") int finalPoints,
            @Param("balanceAfter") int balanceAfter, @Param("expiresAt") LocalDateTime expiresAt,
            @Param("title") String title, @Param("description") String description);

    @Update("UPDATE points_transaction SET status = 3 WHERE id = #{id} AND order_sn = #{orderSn} AND status = 2")
    int revertRedeem(@Param("id") Long id, @Param("orderSn") String orderSn);

    @Select("SELECT COALESCE(SUM(points), 0) FROM points_transaction WHERE member_id = #{memberId} AND status = 2")
    int sumConfirmedPoints(@Param("memberId") Long memberId);

    @Select("SELECT COALESCE(SUM(points), 0) FROM points_transaction WHERE member_id = #{memberId} AND status = 1")
    int sumPendingPoints(@Param("memberId") Long memberId);

    @Select("SELECT COALESCE(SUM(points), 0) FROM points_transaction WHERE member_id = #{memberId} AND type = 1 "
            + "AND status = 2 AND created_at >= #{monthStart}")
    int sumConfirmedEarnPointsSince(@Param("memberId") Long memberId, @Param("monthStart") LocalDateTime monthStart);

    /** points > 0——封頂封到 0 點的列不算「有效批次」，0 點沒有東西可以到期，不該出現在到期清單裡。 */
    @Select("SELECT * FROM points_transaction pt WHERE pt.member_id = #{memberId} AND pt.type = 1 AND pt.status = 2 "
            + "AND pt.points > 0 AND NOT EXISTS (SELECT 1 FROM points_transaction e WHERE e.type = 3 "
            + "AND e.related_transaction_id = pt.id) ORDER BY pt.expires_at ASC")
    List<PointsTransactionPO> findActiveEarnBatches(@Param("memberId") Long memberId);

    @Select("SELECT * FROM points_transaction pt WHERE pt.type = 1 AND pt.status = 2 AND pt.points > 0 "
            + "AND pt.expires_at <= #{now} AND NOT EXISTS (SELECT 1 FROM points_transaction e WHERE e.type = 3 "
            + "AND e.related_transaction_id = pt.id) ORDER BY pt.expires_at ASC LIMIT #{limit}")
    List<PointsTransactionPO> findExpirableEarnBatches(@Param("now") LocalDateTime now, @Param("limit") int limit);

    @Select("SELECT type, status, COUNT(*) AS cnt FROM points_transaction WHERE member_id = #{memberId} "
            + "GROUP BY type, status")
    List<TypeStatusCountRow> countGroupedByTypeAndStatus(@Param("memberId") Long memberId);
}
