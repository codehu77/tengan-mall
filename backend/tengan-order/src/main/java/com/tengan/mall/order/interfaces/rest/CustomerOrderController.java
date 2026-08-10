package com.tengan.mall.order.interfaces.rest;

import com.tengan.mall.order.application.order.CancelOrderCommand;
import com.tengan.mall.order.application.order.CancelOrderUseCase;
import com.tengan.mall.order.application.order.ConfirmOrderUseCase;
import com.tengan.mall.order.application.order.ConfirmReceiptCommand;
import com.tengan.mall.order.application.order.ConfirmReceiptUseCase;
import com.tengan.mall.order.application.order.CreateOrderCommand;
import com.tengan.mall.order.application.order.CreateOrderUseCase;
import com.tengan.mall.order.application.order.GetMyOrderDetailUseCase;
import com.tengan.mall.order.application.order.ListMyOrdersQuery;
import com.tengan.mall.order.application.order.ListMyOrdersUseCase;
import com.tengan.mall.order.application.order.ReceiverInfo;
import com.tengan.mall.order.interfaces.rest.dto.ConfirmedItemResponse;
import com.tengan.mall.order.interfaces.rest.dto.CreateOrderRequest;
import com.tengan.mall.order.interfaces.rest.dto.CreateOrderResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderConfirmResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderDetailResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderItemResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderListResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** zero-trust：直接用 userJwtDecoder 驗證的 Jwt 取 sub 當 memberId，不信任 Gateway 轉發的任何明文身份資訊。 */
@RestController
@RequestMapping("/api/customer/orders")
public class CustomerOrderController {

    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final ListMyOrdersUseCase listMyOrdersUseCase;
    private final GetMyOrderDetailUseCase getMyOrderDetailUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ConfirmReceiptUseCase confirmReceiptUseCase;

    public CustomerOrderController(ConfirmOrderUseCase confirmOrderUseCase, CreateOrderUseCase createOrderUseCase,
            ListMyOrdersUseCase listMyOrdersUseCase, GetMyOrderDetailUseCase getMyOrderDetailUseCase,
            CancelOrderUseCase cancelOrderUseCase, ConfirmReceiptUseCase confirmReceiptUseCase) {
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.createOrderUseCase = createOrderUseCase;
        this.listMyOrdersUseCase = listMyOrdersUseCase;
        this.getMyOrderDetailUseCase = getMyOrderDetailUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.confirmReceiptUseCase = confirmReceiptUseCase;
    }

    @GetMapping("/confirm")
    public OrderConfirmResponse confirm(@AuthenticationPrincipal Jwt jwt) {
        var result = confirmOrderUseCase.confirm(memberId(jwt));
        var items = result.items().stream()
                .map(i -> new ConfirmedItemResponse(i.skuId(), i.spuId(), i.name(), i.mainImage(), i.price(),
                        i.count(), i.subtotal()))
                .toList();
        return new OrderConfirmResponse(result.orderToken(), items, result.totalAmount());
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateOrderRequest request) {
        var receiverInfo = new ReceiverInfo(request.receiverInfo().receiverName(),
                request.receiverInfo().receiverPhone(), request.receiverInfo().city(),
                request.receiverInfo().district(), request.receiverInfo().postalCode(),
                request.receiverInfo().street());
        var result = createOrderUseCase.create(new CreateOrderCommand(memberId(jwt), request.orderToken(),
                receiverInfo, request.paymentMethod(), request.couponId(), request.remark()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateOrderResponse(result.orderSn(), result.payAmount()));
    }

    @GetMapping
    public OrderListResponse list(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        var result = listMyOrdersUseCase.list(new ListMyOrdersQuery(memberId(jwt), status, page, pageSize));
        var items = result.items().stream()
                .map(s -> new OrderSummaryResponse(s.id(), s.orderSn(), s.memberId(), s.status(), s.payAmount(),
                        s.paymentMethod(), s.createdAt()))
                .toList();
        return new OrderListResponse(items, result.total());
    }

    @GetMapping("/{orderSn}")
    public OrderDetailResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable String orderSn) {
        var d = getMyOrderDetailUseCase.get(memberId(jwt), orderSn);
        var items = d.items().stream()
                .map(i -> new OrderItemResponse(i.skuId(), i.spuId(), i.skuName(), i.skuImage(), i.price(),
                        i.count(), i.subtotal()))
                .toList();
        return new OrderDetailResponse(d.id(), d.orderSn(), d.memberId(), d.status(), d.cancelReason(),
                d.totalAmount(), d.discountAmount(), d.payAmount(), d.paymentMethod(), d.couponId(),
                d.receiverName(), d.receiverPhone(), d.city(), d.district(), d.postalCode(), d.street(), d.remark(),
                d.receiptTime(), d.createdAt(), items);
    }

    @PutMapping("/{orderSn}/cancel")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable String orderSn) {
        cancelOrderUseCase.cancel(new CancelOrderCommand(memberId(jwt), orderSn));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderSn}/confirm-receipt")
    public ResponseEntity<Void> confirmReceipt(@AuthenticationPrincipal Jwt jwt, @PathVariable String orderSn) {
        confirmReceiptUseCase.confirm(new ConfirmReceiptCommand(memberId(jwt), orderSn));
        return ResponseEntity.noContent().build();
    }

    private Long memberId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
