package com.tengan.mall.payment.domain.repository;

import com.tengan.mall.payment.domain.model.PaymentRecord;
import java.util.List;
import java.util.Optional;

public interface PaymentRecordRepository {

    PaymentRecord save(PaymentRecord record);

    Optional<PaymentRecord> findByOrderSn(String orderSn);

    /** 條件式 UPDATE status=PAID, gateway_trade_no=?, paid_at=NOW() WHERE order_sn=? AND status=PENDING，回傳是否真的搶到操作權。 */
    boolean markPaid(String orderSn, String gatewayTradeNo);

    /** 只刪還沒付款成功的舊記錄（狀態=PENDING），讓使用者可以換付款方式重新發起——PAID 記錄永遠不會被這支刪掉。 */
    void deleteIfPending(String orderSn);

    /** 供 admin 列表查詢，orderSn/method 皆可為 null（不篩選）。 */
    List<PaymentRecord> search(String orderSn, String method, int page, int pageSize);

    long countSearch(String orderSn, String method);
}
