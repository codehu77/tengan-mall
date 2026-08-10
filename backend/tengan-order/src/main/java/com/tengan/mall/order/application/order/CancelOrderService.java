package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.CouponPort;
import com.tengan.mall.order.application.port.InventoryPort;
import com.tengan.mall.order.domain.exception.OrderAccessDeniedException;
import com.tengan.mall.order.domain.exception.OrderCancellationNotAllowedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

/** 顧客本人取消——只允許 PENDING_PAYMENT（已付款不能取消，見「退款刻意不做」定案）。 */
@Service
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final InventoryPort inventoryPort;
    private final CouponPort couponPort;

    public CancelOrderService(OrderRepository orderRepository, InventoryPort inventoryPort, CouponPort couponPort) {
        this.orderRepository = orderRepository;
        this.inventoryPort = inventoryPort;
        this.couponPort = couponPort;
    }

    @Override
    public void cancel(CancelOrderCommand command) {
        var order = orderRepository.findByOrderSn(command.orderSn())
                .orElseThrow(() -> new OrderNotFoundException(command.orderSn()));
        if (!order.getMemberId().equals(command.memberId())) {
            throw new OrderAccessDeniedException(command.orderSn());
        }
        if (!orderRepository.markCancelled(command.orderSn(), "USER_CANCELLED")) {
            throw new OrderCancellationNotAllowedException(command.orderSn());
        }
        inventoryPort.release(command.orderSn());
        if (order.getCouponId() != null) {
            couponPort.revert(order.getCouponId(), command.orderSn());
        }
    }
}
