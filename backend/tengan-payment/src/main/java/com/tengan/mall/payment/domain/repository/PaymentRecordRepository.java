package com.tengan.mall.payment.domain.repository;

import com.tengan.mall.payment.domain.model.PaymentRecord;
import java.util.List;
import java.util.Optional;

public interface PaymentRecordRepository {

    PaymentRecord save(PaymentRecord record);

    /** 這張訂單已經確定付款成功的那一筆（至多一筆——真正付款成功後不會再有第二次嘗試）。 */
    Optional<PaymentRecord> findPaidByOrderSn(String orderSn);

    /** 供狀態查詢/LINE Pay confirm 用：PAID 優先，否則回傳最新一筆（任何狀態）。 */
    Optional<PaymentRecord> findLatestByOrderSn(String orderSn);

    /** 供 credit_card 重試流程用：這張訂單目前還卡在 PENDING 的最新一筆 credit_card 嘗試。 */
    Optional<PaymentRecord> findLatestPendingCreditCardByOrderSn(String orderSn);

    /** 供 ECPay callback 反查對應記錄——callback 帶的 MerchantTradeNo 現在是這個欄位，不是 orderSn。 */
    Optional<PaymentRecord> findByEcpayMerchantTradeNo(String ecpayMerchantTradeNo);

    /** 供事後偵測「同一張訂單是否有多筆 PAID」用（見 Phase 8.6 擴充的殘餘風險偵測）。 */
    List<PaymentRecord> findAllPaidByOrderSn(String orderSn);

    /** 條件式 UPDATE status=PAID, gateway_trade_no=?, paid_at=NOW() WHERE id=? AND status=PENDING，回傳是否真的搶到操作權。 */
    boolean markPaid(Long id, String gatewayTradeNo);

    /** 條件式 UPDATE status=FAILED WHERE id=? AND status=PENDING，同步查帳確認未付款時呼叫。 */
    boolean markFailed(Long id);

    /** 只給 cod/linepay 用，刪還沒付款成功的舊記錄（狀態=PENDING）——PAID 記錄永遠不會被這支刪掉。 */
    void deleteIfPending(String orderSn);

    /** 供 admin 列表查詢，orderSn/method 皆可為 null（不篩選）。 */
    List<PaymentRecord> search(String orderSn, String method, int page, int pageSize);

    long countSearch(String orderSn, String method);
}
