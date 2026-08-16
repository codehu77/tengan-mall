package com.tengan.mall.wallet.domain.repository;

import com.tengan.mall.wallet.domain.model.PointsTransaction;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PointsTransactionRepository {

    PointsTransaction save(PointsTransaction transaction);

    Optional<PointsTransaction> findById(Long id);

    Optional<PointsTransaction> findPendingEarnByOrderSn(String orderSn);

    /**
     * 查這個 orderSn 底下「任何狀態」的 EARN 列（PENDING 或 CONFIRMED），reserve/earn 兩邊的冪等判斷
     * 都要用這支，不能只查 PENDING——一旦這筆已經被 earn() 轉成 CONFIRMED，重複呼叫 reserve/earn 時
     * 「查 PENDING」會查不到而誤判成「還沒 reserve 過」，因而補插入一筆重複的列。
     */
    Optional<PointsTransaction> findEarnByOrderSn(String orderSn);

    /**
     * 條件式 UPDATE PENDING→CONFIRMED，套用當月封頂後的最終點數/到期日，回傳是否真的搶到確認權。
     * 一定要呼叫（即使 finalPoints 被封頂成 0）——PENDING 列不結案的話會永久卡住，tengan-order 卻已經
     * 把 points_credited 標記為 true 不會再重掃這張訂單，之後也沒有機制回頭處理。description 用來
     * 記錄「原本可得多少、為什麼被封頂」，讓封頂不是資料無聲消失。
     */
    boolean confirmPending(String orderSn, int finalPoints, int balanceAfter, Instant expiresAt, String title,
            String description);

    Optional<PointsTransaction> findConfirmedRedeemByOrderSn(Long memberId, String orderSn);

    /** 條件式 UPDATE CONFIRMED→REVERSED，比照 MemberCoupon 的 consume/revert，冪等 no-op 安全。 */
    boolean revertRedeem(Long id, String orderSn);

    /** 可用餘額：SUM(points) WHERE status=CONFIRMED。 */
    int sumConfirmedPoints(Long memberId);

    /** 待入帳點數：SUM(points) WHERE status=PENDING（只有 EARN 會停在這個狀態）。 */
    int sumPendingPoints(Long memberId);

    /** 當月封頂判斷用：本月已入帳（CONFIRMED）的 EARN 點數總和。 */
    int sumConfirmedEarnPointsSince(Long memberId, Instant monthStart);

    /** 尚未被沖銷（無對應 EXPIRE 列）的 CONFIRMED EARN 批次，依到期日由近到遠排序。 */
    List<PointsTransaction> findActiveEarnBatches(Long memberId);

    /** 全站範圍內已到期但尚未沖銷的 EARN 批次，供 PointsExpiryScheduler 掃描。 */
    List<PointsTransaction> findExpirableEarnBatches(Instant now, int limit);

    /**
     * status 為 null 代表不限狀態。前端「待入帳」篩選在 type 找不到對應值（PENDING 是 status 不是
     * type），呼叫端要轉譯成 type=null+status=PENDING；「獲得」篩選則要轉譯成 type=EARN+
     * status=CONFIRMED，不然還沒轉正的 PENDING 列會同時出現在「獲得」跟「待入帳」兩個篩選底下
     * （同一筆交易被誤判成兩種不同分類，曾經在這裡踩過的坑）。
     */
    List<PointsTransaction> search(Long memberId, Integer type, Integer status, Instant fromDate, String keyword,
            int pageNum, int pageSize);

    long countSearch(Long memberId, Integer type, Integer status, Instant fromDate, String keyword);

    /**
     * 這個會員底下所有 (type, status) 組合各自的筆數，交易明細分類 tabs 顯示筆數用。回傳原始分組結果，
     * 不在這裡假設前端有哪幾個具名分類——分類定義（例如「已過期」對應 type=EXPIRE 不分狀態）留給呼叫端
     * 自己組合，避免分類定義同時存在後端跟前端兩個地方、之後改分類要兩邊同步。
     */
    List<TypeStatusCount> countGroupedByTypeAndStatus(Long memberId);
}
