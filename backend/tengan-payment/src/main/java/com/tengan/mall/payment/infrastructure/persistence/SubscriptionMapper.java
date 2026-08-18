package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SubscriptionMapper extends BaseMapper<SubscriptionPO> {

    // 扣款成功一定代表這份訂閱「現在算數」，順便把 status=0(PENDING) 升成 1(ACTIVE)——首期的首刷確認
    // 跟後續每期續約成功共用同一支方法，PENDING 只會出現在首期，ACTIVE/CANCELLED 這裡維持原樣不動
    // （CANCELLED 的情況是使用者已取消、但這期本來就付過了，paid_until 仍要延長，不代表要復活訂閱）。
    @Update("UPDATE subscription SET paid_until = #{paidUntil}, consecutive_failures = 0, "
            + "status = CASE WHEN status = 0 THEN 1 ELSE status END WHERE id = #{id}")
    int extendPaidUntil(@Param("id") Long id, @Param("paidUntil") LocalDateTime paidUntil);

    // status <> 2：PENDING(0) 或 ACTIVE(1) 都能轉 CANCELLED——PENDING 轉來自首刷失敗（見
    // HandleSubscriptionReturnCallbackService），ACTIVE 轉來自使用者主動取消或連續失敗 6 次停用。
    @Update("UPDATE subscription SET status = 2, cancelled_at = NOW() WHERE id = #{id} AND status <> 2")
    int markCancelled(@Param("id") Long id);

    @Update("UPDATE subscription SET consecutive_failures = consecutive_failures + 1 WHERE id = #{id}")
    int incrementConsecutiveFailures(@Param("id") Long id);

    // status=2(CANCELLED) 限定：ACTIVE 代表 ECPay 還在繼續扣款，只是這期的 PeriodReturnURL 通知可能還
    // 沒送達（實測過會延遲到隔天），paid_until 暫時過期不代表真的不續訂了，不能降級；只有顧客主動取消
    // 或連續失敗 6 次被停用（這兩種都會轉成 CANCELLED）才是真的到期不續，才輪到這裡處理。
    @Select("SELECT * FROM subscription WHERE status = 2 AND paid_until <= #{now} AND benefit_expired_at IS NULL "
            + "ORDER BY paid_until ASC LIMIT #{limit}")
    List<SubscriptionPO> findDueForExpiry(@Param("now") LocalDateTime now, @Param("limit") int limit);

    // 順便清 active_slot，讓 uk_active_slot 這個「同一會員同時間只能一筆現在算數的訂閱」的唯一鍵
    // 空出來，會員才能重新訂閱（見 uk_active_slot 的說明）。
    @Update("UPDATE subscription SET benefit_expired_at = NOW(), active_slot = NULL "
            + "WHERE id = #{id} AND benefit_expired_at IS NULL")
    int markBenefitExpired(@Param("id") Long id);

    @Select("SELECT * FROM subscription WHERE member_id = #{memberId} AND benefit_expired_at IS NULL "
            + "ORDER BY created_at DESC LIMIT 1")
    SubscriptionPO findCurrentByMemberId(@Param("memberId") Long memberId);
}
