package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.OrderPort;
import com.tengan.mall.admin.interfaces.rest.dto.CancelOrderRequest;
import com.tengan.mall.admin.interfaces.rest.dto.ListOrdersResponse;
import com.tengan.mall.admin.interfaces.rest.dto.OrderDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.OrderItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.OrderSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-order 的 /internal/orders，跟 {@link ProductBrandController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/orders")
public class OrderController {

    private final OrderPort orderPort;

    public OrderController(OrderPort orderPort) {
        this.orderPort = orderPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('order:list:read')")
    public ListOrdersResponse list(@RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int pageSize) {
        var result = orderPort.listOrders(status, page, pageSize);
        var items = result.items().stream()
                .map(o -> new OrderSummaryResponse(o.id(), o.orderSn(), o.memberId(), o.status(), o.payAmount(),
                        o.paymentMethod(), o.createdAt()))
                .toList();
        return new ListOrdersResponse(items, result.total());
    }

    @GetMapping("/{orderSn}")
    @PreAuthorize("hasAuthority('order:list:read')")
    public OrderDetailResponse detail(@PathVariable String orderSn) {
        var d = orderPort.getOrderDetail(orderSn);
        var items = d.items().stream()
                .map(i -> new OrderItemResponse(i.skuId(), i.spuId(), i.skuName(), i.skuImage(), i.price(),
                        i.count(), i.subtotal()))
                .toList();
        return new OrderDetailResponse(d.id(), d.orderSn(), d.memberId(), d.status(), d.cancelReason(),
                d.totalAmount(), d.discountAmount(), d.payAmount(), d.paymentMethod(), d.couponId(),
                d.receiverName(), d.receiverPhone(), d.city(), d.district(), d.postalCode(), d.street(), d.remark(),
                d.receiptTime(), d.createdAt(), items);
    }

    @PutMapping("/{orderSn}/ship")
    @PreAuthorize("hasAuthority('order:ship:write')")
    public ResponseEntity<Void> ship(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable String orderSn) {
        orderPort.shipOrder(orderSn, operatorJwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderSn}/cancel")
    @PreAuthorize("hasAuthority('order:cancel:write')")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable String orderSn,
            @Valid @RequestBody CancelOrderRequest request) {
        orderPort.cancelOrder(orderSn, request.reason(), operatorJwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }
}
