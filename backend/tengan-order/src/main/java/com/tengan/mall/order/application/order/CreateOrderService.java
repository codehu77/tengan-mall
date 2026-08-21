package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.ActiveSeckillSku;
import com.tengan.mall.order.application.port.CartPort;
import com.tengan.mall.order.application.port.CheckedCartItem;
import com.tengan.mall.order.application.port.CouponPort;
import com.tengan.mall.order.application.port.InventoryPort;
import com.tengan.mall.order.application.port.LockItem;
import com.tengan.mall.order.application.port.OrderEventPort;
import com.tengan.mall.order.application.port.OrderTokenPort;
import com.tengan.mall.order.application.port.PricedSkuInfo;
import com.tengan.mall.order.application.port.ProductPort;
import com.tengan.mall.order.application.port.SeckillPort;
import com.tengan.mall.order.application.port.WalletPort;
import com.tengan.mall.order.domain.exception.CouponNotApplicableException;
import com.tengan.mall.order.domain.exception.EmptyCartException;
import com.tengan.mall.order.domain.exception.InventoryShortageException;
import com.tengan.mall.order.domain.exception.OrderTokenInvalidException;
import com.tengan.mall.order.domain.model.Order;
import com.tengan.mall.order.domain.model.OrderItem;
import com.tengan.mall.order.domain.repository.OrderRepository;
import com.tengan.mall.snowflake.SnowflakeIdGenerator;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
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
 * <p>步驟排序刻意「先做便宜可逆的、後做昂貴不可逆的」：秒殺配額保留（Phase 9 新增，購物車裡若有
 * 秒殺項目，這是最稀缺、競爭最激烈的資源，優先嘗試盡早失敗）→ 鎖一般庫存（失敗機率相對高，失敗時
 * 什麼都還沒發生）→ 核銷優惠券（失敗機率較低，一旦成功就有副作用）→ 本地寫入 order/order_item
 * （機率最低，一旦要處理失敗要補償前面各項）。補償堆疊（compensations）記錄「已成功、失敗時需要
 * 反做」的動作，由外而內反序執行——這是手動實作的 Saga 補償鏈，取代分散式交易（見規劃文件一、1~4、7；
 * 秒殺配額保留見 Phase 9 規劃第 4.2 節）。</p>
 */
@Service
public class CreateOrderService implements CreateOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderService.class);

    private final OrderTokenPort orderTokenPort;
    private final CartPort cartPort;
    private final ProductPort productPort;
    private final CouponPort couponPort;
    private final WalletPort walletPort;
    private final InventoryPort inventoryPort;
    private final SeckillPort seckillPort;
    private final OrderRepository orderRepository;
    private final OrderEventPort orderEventPort;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public CreateOrderService(OrderTokenPort orderTokenPort, CartPort cartPort, ProductPort productPort,
            CouponPort couponPort, WalletPort walletPort, InventoryPort inventoryPort, SeckillPort seckillPort,
            OrderRepository orderRepository, OrderEventPort orderEventPort, SnowflakeIdGenerator snowflakeIdGenerator) {
        this.orderTokenPort = orderTokenPort;
        this.cartPort = cartPort;
        this.productPort = productPort;
        this.couponPort = couponPort;
        this.walletPort = walletPort;
        this.inventoryPort = inventoryPort;
        this.seckillPort = seckillPort;
        this.orderRepository = orderRepository;
        this.orderEventPort = orderEventPort;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
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
        // 秒殺項目跟一般商品長得一樣（同一顆 SKU 只會展示一種樣子），這裡先問過 tengan-seckill 目前
        // 誰是活躍秒殺，價格改用秒殺價，名稱/圖片仍照舊查 tengan-product（見 Phase 9 規劃第 4.2 節）。
        Map<Long, ActiveSeckillSku> activeSeckillBySkuId = seckillPort.checkActive(skuIds);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            PricedSkuInfo sku = priceBySkuId.get(cartItem.skuId());
            ActiveSeckillSku seckill = activeSeckillBySkuId.get(cartItem.skuId());
            BigDecimal unitPrice = seckill != null ? seckill.seckillPrice() : sku.price();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.count()));
            return new OrderItem(null, sku.skuId(), sku.spuId(), sku.name(), sku.mainImage(), unitPrice,
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
        String orderSn = generateOrderSn();

        // 4. 補償堆疊：由外而內記錄「已成功、失敗時需要反做」的動作
        Deque<Runnable> compensations = new ArrayDeque<>();
        BigDecimal pointsDiscountAmount = BigDecimal.ZERO;
        BigDecimal payAmount;
        try {
            // 秒殺項目最先處理（最可能失敗、競爭最激烈的資源），優先嘗試，搶不到就盡早失敗，
            // 不浪費後面一般商品鎖庫存的成本（見 Phase 9 規劃第 4.2 節「處理順序」）。
            for (CheckedCartItem cartItem : cartItems) {
                if (!activeSeckillBySkuId.containsKey(cartItem.skuId())) {
                    continue;
                }
                Long skuId = cartItem.skuId();
                int count = cartItem.count();
                Long memberId = command.memberId();
                seckillPort.reserve(skuId, memberId, count);
                compensations.push(() -> seckillPort.release(skuId, memberId, count));
            }

            // 一般商品只鎖「不是秒殺」的那些——秒殺項目全程不動真實庫存，只在場次結束後的結算
            // 步驟一次性同步實際賣出量（見 Phase 9 規劃第 3、6 節）。
            var lockItems = cartItems.stream().filter(item -> !activeSeckillBySkuId.containsKey(item.skuId()))
                    .map(item -> new LockItem(item.skuId(), item.count())).toList();
            if (!lockItems.isEmpty()) {
                var lockResult = inventoryPort.lock(orderSn, lockItems);
                if (!lockResult.success()) {
                    throw new InventoryShortageException(lockResult.shortageSkuIds());
                }
                compensations.push(() -> inventoryPort.release(orderSn));
            }

            if (command.couponId() != null) {
                couponPort.consume(command.couponId(), orderSn);
                Long couponId = command.couponId();
                compensations.push(() -> couponPort.revert(couponId, orderSn));
            }

            // 5. 若使用點數折抵，向 tengan-wallet 核銷（失敗機率比核銷優惠券更低——餘額不足才會失敗，
            // 放在庫存/優惠券之後、寫入本地 order 之前，沿用「先做便宜可逆、後做昂貴不可逆」排序原則）
            if (command.pointsUsed() != null && command.pointsUsed() > 0) {
                var walletResult = walletPort.consume(command.memberId(), command.pointsUsed(), orderSn);
                pointsDiscountAmount = walletResult.discountAmount();
                Long memberId = command.memberId();
                compensations.push(() -> walletPort.revert(memberId, orderSn));
            }

            payAmount = totalAmount.subtract(discountAmount).subtract(pointsDiscountAmount);
            ReceiverInfo receiver = command.receiverInfo();
            Order order = Order.create(orderSn, command.memberId(), command.paymentMethod(), command.couponId(),
                    command.pointsUsed(), pointsDiscountAmount, receiver.receiverName(), receiver.receiverPhone(),
                    receiver.city(), receiver.district(), receiver.postalCode(), receiver.street(), command.remark(),
                    totalAmount, discountAmount, payAmount, orderItems);
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

        // 6. 非關鍵路徑：訂單已經成立，這兩步失敗不該讓下單請求失敗（已知限制，見規劃文件五、）
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

    /**
     * 用 Snowflake 產生，保證唯一（不用 DB 序列）。原本是 timestamp+random 的機率性防碰撞，
     * 秒殺場景下同一秒可能有上百筆訂單同時產生，碰撞機率不可忽略，改用 tengan-snowflake-starter。
     */
    private String generateOrderSn() {
        return Long.toString(snowflakeIdGenerator.nextId());
    }
}
