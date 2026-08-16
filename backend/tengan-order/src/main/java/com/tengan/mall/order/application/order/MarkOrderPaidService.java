package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.OrderEventPort;
import com.tengan.mall.order.domain.exception.OrderMarkPaidNotAllowedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import com.tengan.mall.order.domain.model.OrderStatus;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * 由 tengan-payment 驗完 ECPay callback／LINE Pay confirm 或 COD 同步完成後呼叫。條件式 UPDATE
 * 搶操作權（{@link OrderRepository#markPaid}）成功才發 order.paid 事件——tengan-inventory 訂閱這個
 * 事件觸發扣庫存（locked→deducted），tengan-order 自己不消費，只當生產者（見 Phase 7 規劃）。
 *
 * <p>冪等：訂單已經是 PAID（重複通知）視為成功 no-op；訂單是 CANCELLED（跟 TTL 逾時取消競速的
 * 已知邊界情況）才真的拋例外，由呼叫端（tengan-payment）決定怎麼處理（記 WARN、不自動退款）。</p>
 */
@Service
public class MarkOrderPaidService implements MarkOrderPaidUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPort orderEventPort;

    public MarkOrderPaidService(OrderRepository orderRepository, OrderEventPort orderEventPort) {
        this.orderRepository = orderRepository;
        this.orderEventPort = orderEventPort;
    }

    @Override
    public void markPaid(String orderSn) {
        var order = orderRepository.findByOrderSn(orderSn).orElseThrow(() -> new OrderNotFoundException(orderSn));

        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }
        if (!orderRepository.markPaid(orderSn)) {
            throw new OrderMarkPaidNotAllowedException(orderSn);
        }
        orderEventPort.publishOrderPaid(orderSn);
    }
}
