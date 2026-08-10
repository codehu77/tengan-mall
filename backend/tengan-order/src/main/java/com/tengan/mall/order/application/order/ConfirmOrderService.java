package com.tengan.mall.order.application.order;

import com.tengan.mall.order.application.port.CartPort;
import com.tengan.mall.order.application.port.OrderTokenPort;
import com.tengan.mall.order.application.port.PricedSkuInfo;
import com.tengan.mall.order.application.port.ProductPort;
import com.tengan.mall.order.domain.exception.EmptyCartException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 只做「只有 tengan-order 才能做的事」：核發 orderToken、彙整購物車已勾選項目+即時價格。
 * 地址清單/優惠券清單刻意不在這裡聚合——前端結帳頁直接呼叫既有的
 * GET /api/customer/member/addresses、GET /api/customer/coupons/available，見規劃文件七、。
 */
@Service
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final CartPort cartPort;
    private final ProductPort productPort;
    private final OrderTokenPort orderTokenPort;

    public ConfirmOrderService(CartPort cartPort, ProductPort productPort, OrderTokenPort orderTokenPort) {
        this.cartPort = cartPort;
        this.productPort = productPort;
        this.orderTokenPort = orderTokenPort;
    }

    @Override
    public OrderConfirmResult confirm(Long memberId) {
        var cartItems = cartPort.getCheckedItems(memberId);
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(memberId);
        }

        List<Long> skuIds = cartItems.stream().map(i -> i.skuId()).toList();
        Map<Long, PricedSkuInfo> priceBySkuId = productPort.batchPrice(skuIds).stream()
                .collect(Collectors.toMap(PricedSkuInfo::skuId, Function.identity()));

        List<ConfirmedItemView> items = cartItems.stream().map(cartItem -> {
            PricedSkuInfo sku = priceBySkuId.get(cartItem.skuId());
            BigDecimal subtotal = sku.price().multiply(BigDecimal.valueOf(cartItem.count()));
            return new ConfirmedItemView(sku.skuId(), sku.spuId(), sku.name(), sku.mainImage(), sku.price(),
                    cartItem.count(), subtotal);
        }).toList();

        BigDecimal totalAmount = items.stream().map(ConfirmedItemView::subtotal).reduce(BigDecimal.ZERO,
                BigDecimal::add);

        String orderToken = orderTokenPort.issue(memberId);
        return new OrderConfirmResult(orderToken, items, totalAmount);
    }
}
