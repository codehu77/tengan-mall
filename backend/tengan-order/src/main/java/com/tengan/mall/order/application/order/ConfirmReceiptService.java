package com.tengan.mall.order.application.order;

import com.tengan.mall.order.domain.exception.OrderAccessDeniedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import com.tengan.mall.order.domain.exception.OrderReceiptNotAllowedException;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * SHIPPED→COMPLETED。這次先建好方法，實際端到端驗證要等 Phase 7（付款/出貨）出現才有真實觸發鏈路
 * 可達 SHIPPED 狀態（見規劃文件十、已知限制）。
 */
@Service
public class ConfirmReceiptService implements ConfirmReceiptUseCase {

    private final OrderRepository orderRepository;

    public ConfirmReceiptService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void confirm(ConfirmReceiptCommand command) {
        var order = orderRepository.findByOrderSn(command.orderSn())
                .orElseThrow(() -> new OrderNotFoundException(command.orderSn()));
        if (!order.getMemberId().equals(command.memberId())) {
            throw new OrderAccessDeniedException(command.orderSn());
        }
        if (!orderRepository.markCompleted(command.orderSn())) {
            throw new OrderReceiptNotAllowedException(command.orderSn());
        }
    }
}
