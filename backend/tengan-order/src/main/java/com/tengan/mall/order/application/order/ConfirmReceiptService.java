package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.WalletPort;
import com.tengan.mall.order.domain.exception.OrderAccessDeniedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import com.tengan.mall.order.domain.exception.OrderReceiptNotAllowedException;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * SHIPPED→COMPLETED。確認收貨成功後非關鍵路徑呼叫 tengan-wallet 的 reserve（Phase 8 新增），
 * 讓「待入帳點數」在 7 天鑑賞期內就看得到；真正入帳是 PointsGrantScheduler 鑑賞期過後才做的事，
 * 這裡失敗不影響確認收貨本身（比照 CreateOrderService 的 safely() 模式）。
 */
@Service
public class ConfirmReceiptService implements ConfirmReceiptUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmReceiptService.class);

    private final OrderRepository orderRepository;
    private final WalletPort walletPort;

    public ConfirmReceiptService(OrderRepository orderRepository, WalletPort walletPort) {
        this.orderRepository = orderRepository;
        this.walletPort = walletPort;
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
        try {
            walletPort.reserve(command.memberId(), command.orderSn(), order.getPayAmount());
        } catch (RuntimeException e) {
            log.error("點數 reserve 失敗，orderSn={}，等 PointsGrantScheduler 鑑賞期過後補建", command.orderSn(), e);
        }
    }
}
