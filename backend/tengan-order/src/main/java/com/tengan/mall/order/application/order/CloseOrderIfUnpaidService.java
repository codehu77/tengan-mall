package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.CouponPort;
import com.tengan.mall.order.application.port.InventoryPort;
import com.tengan.mall.order.application.port.WalletPort;
import com.tengan.mall.order.domain.model.OrderStatus;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * 冪等關單，同時給 RabbitMQ order.close.queue 消費者（同一服務內直接呼叫，不必繞 HTTP）跟未來
 * Phase 7 的 PUT /internal/orders/{orderSn}/close-if-unpaid（供 tengan-payment 逾時通知呼叫）共用，
 * 避免兩個入口各自實作出不一致的關單邏輯（見規劃文件六、）。
 */
@Service
public class CloseOrderIfUnpaidService implements CloseOrderIfUnpaidUseCase {

    private final OrderRepository orderRepository;
    private final InventoryPort inventoryPort;
    private final CouponPort couponPort;
    private final WalletPort walletPort;

    public CloseOrderIfUnpaidService(OrderRepository orderRepository, InventoryPort inventoryPort,
            CouponPort couponPort, WalletPort walletPort) {
        this.orderRepository = orderRepository;
        this.inventoryPort = inventoryPort;
        this.couponPort = couponPort;
        this.walletPort = walletPort;
    }

    @Override
    public void close(String orderSn) {
        orderRepository.findByOrderSn(orderSn).ifPresent(order -> {
            if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
                return; // 已付款（或已被關過）是 no-op，不是錯誤——見規劃文件冪等性說明
            }
            if (!orderRepository.markCancelled(orderSn, "TIMEOUT")) {
                return; // 沒搶到操作權，代表已被別的呼叫處理過（條件式 UPDATE 防重複釋放）
            }
            inventoryPort.release(orderSn);
            if (order.getCouponId() != null) {
                couponPort.revert(order.getCouponId(), orderSn);
            }
            if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                walletPort.revert(order.getMemberId(), orderSn);
            }
        });
    }
}
