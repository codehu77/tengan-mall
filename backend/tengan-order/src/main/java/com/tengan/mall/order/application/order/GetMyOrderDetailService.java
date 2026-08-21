package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.SeckillOrderPendingPort;
import com.tengan.mall.order.domain.exception.OrderAccessDeniedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import com.tengan.mall.order.domain.exception.OrderProcessingException;
import org.springframework.stereotype.Service;

/**
 * 不洩漏「訂單存在但不是你的」——不屬於自己的訂單一律當 404 處理（見 OrderAccessDeniedException）。
 * Phase 9：DB 查無資料時，多查一次秒殺訂單的待處理標記，區分「還在非同步落地中」跟「真的查無此單」
 * （見 Phase 9 規劃第 5 節）。
 */
@Service
public class GetMyOrderDetailService implements GetMyOrderDetailUseCase {

    private final OrderQueryPort orderQueryPort;
    private final SeckillOrderPendingPort seckillOrderPendingPort;

    public GetMyOrderDetailService(OrderQueryPort orderQueryPort, SeckillOrderPendingPort seckillOrderPendingPort) {
        this.orderQueryPort = orderQueryPort;
        this.seckillOrderPendingPort = seckillOrderPendingPort;
    }

    @Override
    public OrderDetailView get(Long memberId, String orderSn) {
        var detailOpt = orderQueryPort.findDetailByOrderSn(orderSn);
        if (detailOpt.isEmpty()) {
            var pendingMemberId = seckillOrderPendingPort.findPendingMemberId(orderSn);
            if (pendingMemberId.isPresent() && pendingMemberId.get().equals(memberId)) {
                throw new OrderProcessingException(orderSn);
            }
            throw new OrderNotFoundException(orderSn);
        }
        var detail = detailOpt.get();
        if (!detail.memberId().equals(memberId)) {
            throw new OrderAccessDeniedException(orderSn);
        }
        return detail;
    }
}
