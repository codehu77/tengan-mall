package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.CartPort;
import com.tengan.mall.order.application.port.CouponPort;
import com.tengan.mall.order.application.port.InventoryPort;
import com.tengan.mall.order.application.port.LockItem;
import com.tengan.mall.order.application.port.OrderEventPort;
import com.tengan.mall.order.application.port.OrderTokenPort;
import com.tengan.mall.order.application.port.PricedSkuInfo;
import com.tengan.mall.order.application.port.ProductPort;
import com.tengan.mall.order.domain.exception.CouponNotApplicableException;
import com.tengan.mall.order.domain.exception.EmptyCartException;
import com.tengan.mall.order.domain.exception.InventoryShortageException;
import com.tengan.mall.order.domain.exception.OrderTokenInvalidException;
import com.tengan.mall.order.domain.model.Order;
import com.tengan.mall.order.domain.model.OrderItem;
import com.tengan.mall.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 下單編排（Saga，編排式）：不標 @Transactional——如果把整段（含呼叫 inventory/coupon 的 HTTP 請求）
 * 包進資料庫交易，會讓連線在等待網路 I/O 期間被閒置佔用，是分散式系統的常見反模式。只有最後
 * orderRepository.save() 那一段本地多表寫入才需要交易保護，已經在 OrderRepositoryImpl.save() 上標好
 * （見規劃文件三、）。
 *
 * <p>步驟排序刻意「先做便宜可逆的、後做昂貴不可逆的」：鎖庫存（失敗機率相對高，失敗時什麼都還沒發生）
 * → 核銷優惠券（失敗機率較低，一旦成功就有副作用）→ 本地寫入 order/order_item（機率最低，一旦要處理
 * 失敗要補償前面兩者）。補償堆疊（compensations）記錄「已成功、失敗時需要反做」的動作，由外而內
 * 反序執行——這是手動實作的 Saga 補償鏈，取代分散式交易（見規劃文件一、1~4、7）。</p>
 */
@Service
public class CreateOrderService implements CreateOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderService.class);
    private static final DateTimeFormatter ORDER_SN_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderTokenPort orderTokenPort;
    private final CartPort cartPort;
    private final ProductPort productPort;
    private final CouponPort couponPort;
    private final InventoryPort inventoryPort;
    private final OrderRepository orderRepository;
    private final OrderEventPort orderEventPort;

    public CreateOrderService(OrderTokenPort orderTokenPort, CartPort cartPort, ProductPort productPort,
            CouponPort couponPort, InventoryPort inventoryPort, OrderRepository orderRepository,
            OrderEventPort orderEventPort) {
        this.orderTokenPort = orderTokenPort;
        this.cartPort = cartPort;
        this.productPort = productPort;
        this.couponPort = couponPort;
        this.inventoryPort = inventoryPort;
        this.orderRepository = orderRepository;
        this.orderEventPort = orderEventPort;
    }

    @Override
    public CreateOrderResult create(CreateOrderCommand command) {
        // 1. orderToken 原子比對刪除（Redis Lua script，見 OrderTokenPort 說明）
        if (!orderTokenPort.consume(command.memberId(), command.orderToken())) {
            throw new OrderTokenInvalidException(command.memberId());
        }

        // 2. 取購物車已勾選項目 + 即時查價（不信任前端金額）
        var cartItems = cartPort.getCheckedItems(command.memberId());
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(command.memberId());
        }
        List<Long> skuIds = cartItems.stream().map(i -> i.skuId()).toList();
        Map<Long, PricedSkuInfo> priceBySkuId = productPort.batchPrice(skuIds).stream()
                .collect(Collectors.toMap(PricedSkuInfo::skuId, Function.identity()));

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            PricedSkuInfo sku = priceBySkuId.get(cartItem.skuId());
            BigDecimal subtotal = sku.price().multiply(BigDecimal.valueOf(cartItem.count()));
            return new OrderItem(null, sku.skuId(), sku.spuId(), sku.name(), sku.mainImage(), sku.price(),
                    cartItem.count(), subtotal);
        }).toList();
        BigDecimal totalAmount = orderItems.stream().map(OrderItem::subtotal).reduce(BigDecimal.ZERO,
                BigDecimal::add);

        // 3. 若帶 couponId，向 tengan-coupon 驗證+算折扣（服務對服務，不信任前端送來的折扣值）
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (command.couponId() != null) {
            var validation = couponPort.validate(command.memberId(), command.couponId(), totalAmount);
            if (!validation.valid()) {
                throw new CouponNotApplicableException(command.couponId());
            }
            discountAmount = validation.discountAmount();
        }
        BigDecimal payAmount = totalAmount.subtract(discountAmount);
        String orderSn = generateOrderSn();

        // 4. 補償堆疊：由外而內記錄「已成功、失敗時需要反做」的動作
        Deque<Runnable> compensations = new ArrayDeque<>();
        try {
            var lockItems = orderItems.stream().map(i -> new LockItem(i.skuId(), i.count())).toList();
            var lockResult = inventoryPort.lock(orderSn, lockItems);
            if (!lockResult.success()) {
                throw new InventoryShortageException(lockResult.shortageSkuIds());
            }
            compensations.push(() -> inventoryPort.release(orderSn));

            if (command.couponId() != null) {
                couponPort.consume(command.couponId(), orderSn);
                Long couponId = command.couponId();
                compensations.push(() -> couponPort.revert(couponId, orderSn));
            }

            ReceiverInfo receiver = command.receiverInfo();
            Order order = Order.create(orderSn, command.memberId(), command.paymentMethod(), command.couponId(),
                    receiver.receiverName(), receiver.receiverPhone(), receiver.city(), receiver.district(),
                    receiver.postalCode(), receiver.street(), command.remark(), totalAmount, discountAmount,
                    payAmount, orderItems);
            // 只有這裡碰資料庫，且只有這裡（OrderRepositoryImpl.save）標 @Transactional
            orderRepository.save(order);

        } catch (RuntimeException e) {
            while (!compensations.isEmpty()) {
                try {
                    compensations.pop().run();
                } catch (RuntimeException compensationFailure) {
                    log.error("補償動作失敗，orderSn={} 需要人工介入", orderSn, compensationFailure);
                }
            }
            throw e;
        }

        // 5. 非關鍵路徑：訂單已經成立，這兩步失敗不該讓下單請求失敗（已知限制，見規劃文件五、）
        safely(() -> orderEventPort.publishOrderCreatedAndScheduleTimeout(orderSn));
        safely(() -> cartPort.removeItemsAfterOrder(command.memberId(), skuIds));

        return new CreateOrderResult(orderSn, payAmount);
    }

    private void safely(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            log.error("下單成功後的非關鍵路徑動作失敗（訂單本身已成立，不影響下單結果）", e);
        }
    }

    /** 服務層產生，不用 DB 序列（比照 PurchaseOrder.poNumber 生成方式）。 */
    private String generateOrderSn() {
        String timestamp = ORDER_SN_TIMESTAMP.format(LocalDateTime.now());
        int random = ThreadLocalRandom.current().nextInt(10000);
        return timestamp + String.format("%04d", random);
    }
}
