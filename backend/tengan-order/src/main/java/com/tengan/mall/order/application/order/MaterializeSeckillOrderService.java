package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.CartPort;
import com.tengan.mall.order.application.port.OrderEventPort;
import com.tengan.mall.order.domain.model.Order;
import com.tengan.mall.order.domain.model.OrderItem;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * order.seckill.order.queue 消費者呼叫的落地邏輯：把 {@code CreateOrderService} 保留成功但還沒寫 DB
 * 的訂單真正 {@code orderRepository.save()}，成功後才觸發逾時排程+清購物車這兩個非關鍵路徑動作
 * （比照 {@code CreateOrderService} 步驟 6 同一組 safely() 呼叫時機，只是延後到訂單真的落地之後才做，
 * 避免在訂單還不存在時就排入逾時取消排程）。
 *
 * <p><b>冪等</b>：MQ 訊息重複投遞是常態，用「orderSn 已存在就跳過」擋住重複寫入，不是拿 unique
 * constraint 擋（那樣重試機制會誤判成失敗），見 Phase 9 規劃第 5 節。</p>
 *
 * <p><b>已知限制</b>：如果這裡的 {@code orderRepository.save()} 持續失敗到 Spring Retry 用盡進了 DLQ，
 * {@code CreateOrderService} 那邊已經保留成功的秒殺配額/一般庫存鎖定不會自動釋放，需要人工介入——
 * 跟 order.close.queue 補償失敗時「記 log 需要人工介入」是同一種已知限制，沒有另外疊一層自動補償。</p>
 */
@Service
public class MaterializeSeckillOrderService implements MaterializeSeckillOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(MaterializeSeckillOrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventPort orderEventPort;
    private final CartPort cartPort;

    public MaterializeSeckillOrderService(OrderRepository orderRepository, OrderEventPort orderEventPort,
            CartPort cartPort) {
        this.orderRepository = orderRepository;
        this.orderEventPort = orderEventPort;
        this.cartPort = cartPort;
    }

    @Override
    public void materialize(MaterializeSeckillOrderCommand command) {
        if (orderRepository.findByOrderSn(command.orderSn()).isPresent()) {
            log.info("秒殺訂單已經落地過，MQ 重複投遞，略過: orderSn={}", command.orderSn());
            return;
        }

        Order order = Order.create(command.orderSn(), command.memberId(), command.paymentMethod(),
                command.couponId(), command.pointsUsed(), command.pointsDiscountAmount(), command.receiverName(),
                command.receiverPhone(), command.city(), command.district(), command.postalCode(), command.street(),
                command.remark(), command.totalAmount(), command.discountAmount(), command.payAmount(),
                command.items());
        orderRepository.save(order);

        safely(() -> orderEventPort.publishOrderCreatedAndScheduleTimeout(command.orderSn()));
        safely(() -> cartPort.removeItemsAfterOrder(command.memberId(),
                command.items().stream().map(OrderItem::skuId).toList()));
    }

    private void safely(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            log.error("秒殺訂單落地後的非關鍵路徑動作失敗（訂單本身已成立，不影響落地結果）", e);
        }
    }
}
