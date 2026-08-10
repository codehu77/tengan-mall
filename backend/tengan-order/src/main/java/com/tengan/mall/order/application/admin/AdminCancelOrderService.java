package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.OrderQueryPort;
import com.tengan.mall.order.application.port.CouponPort;
import com.tengan.mall.order.application.port.InventoryPort;
import com.tengan.mall.order.domain.exception.OrderCancellationNotAllowedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import com.tengan.mall.order.domain.model.OrderOperLog;
import com.tengan.mall.order.domain.repository.OrderOperLogRepository;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * 客服代為取消——跟顧客自己的 CancelOrderService 共用 OrderRepository 同一個條件式 UPDATE
 * （markCancelled），差別在授權規則（不檢查歸屬，客服本來就能操作任何人的訂單）跟需要記稽核+取消原因
 * （見 ddd-standards.md 第四節「前後台拆 use case 資料夾」判斷準則、後台API文件「訂單管理」）。
 */
@Service
public class AdminCancelOrderService implements AdminCancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderQueryPort orderQueryPort;
    private final InventoryPort inventoryPort;
    private final CouponPort couponPort;
    private final OrderOperLogRepository orderOperLogRepository;

    public AdminCancelOrderService(OrderRepository orderRepository, OrderQueryPort orderQueryPort,
            InventoryPort inventoryPort, CouponPort couponPort, OrderOperLogRepository orderOperLogRepository) {
        this.orderRepository = orderRepository;
        this.orderQueryPort = orderQueryPort;
        this.inventoryPort = inventoryPort;
        this.couponPort = couponPort;
        this.orderOperLogRepository = orderOperLogRepository;
    }

    @Override
    public void cancel(AdminCancelOrderCommand command) {
        var detail = orderQueryPort.findDetailByOrderSn(command.orderSn())
                .orElseThrow(() -> new OrderNotFoundException(command.orderSn()));
        if (!orderRepository.markCancelled(command.orderSn(), "ADMIN_CANCELLED")) {
            throw new OrderCancellationNotAllowedException(command.orderSn());
        }
        inventoryPort.release(command.orderSn());
        if (detail.couponId() != null) {
            couponPort.revert(detail.couponId(), command.orderSn());
        }
        orderOperLogRepository.save(OrderOperLog.create(command.operator(), "cancel",
                "客服代為取消 orderSn=" + command.orderSn() + " reason=" + command.reason()));
    }
}
