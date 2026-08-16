package com.tengan.mall.order.interfaces.rest;

import com.tengan.mall.order.application.admin.AdminCancelOrderCommand;
import com.tengan.mall.order.application.admin.AdminCancelOrderUseCase;
import com.tengan.mall.order.application.admin.AdminGetOrderDetailUseCase;
import com.tengan.mall.order.application.admin.AdminListOrdersQuery;
import com.tengan.mall.order.application.admin.AdminListOrdersUseCase;
import com.tengan.mall.order.application.admin.GetOrderStatsTodayUseCase;
import com.tengan.mall.order.application.admin.ShipOrderCommand;
import com.tengan.mall.order.application.admin.ShipOrderUseCase;
import com.tengan.mall.order.application.order.CloseOrderIfUnpaidUseCase;
import com.tengan.mall.order.application.order.MarkOrderPaidUseCase;
import com.tengan.mall.order.interfaces.rest.dto.AdminCancelOrderRequest;
import com.tengan.mall.order.interfaces.rest.dto.OrderDetailResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderItemResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderListResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderStatsTodayResponse;
import com.tengan.mall.order.interfaces.rest.dto.OrderSummaryResponse;
import com.tengan.mall.jwt.IdentityAssertionVerifier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 供 tengan-admin BFF 呼叫，見 微服務後台API待開發清單.md「訂單管理」章節。 */
@RestController
@RequestMapping("/internal/orders")
public class InternalOrderController {

    private final AdminListOrdersUseCase adminListOrdersUseCase;
    private final AdminGetOrderDetailUseCase adminGetOrderDetailUseCase;
    private final GetOrderStatsTodayUseCase getOrderStatsTodayUseCase;
    private final ShipOrderUseCase shipOrderUseCase;
    private final AdminCancelOrderUseCase adminCancelOrderUseCase;
    private final CloseOrderIfUnpaidUseCase closeOrderIfUnpaidUseCase;
    private final MarkOrderPaidUseCase markOrderPaidUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalOrderController(AdminListOrdersUseCase adminListOrdersUseCase,
            AdminGetOrderDetailUseCase adminGetOrderDetailUseCase, GetOrderStatsTodayUseCase getOrderStatsTodayUseCase,
            ShipOrderUseCase shipOrderUseCase, AdminCancelOrderUseCase adminCancelOrderUseCase,
            CloseOrderIfUnpaidUseCase closeOrderIfUnpaidUseCase, MarkOrderPaidUseCase markOrderPaidUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.adminListOrdersUseCase = adminListOrdersUseCase;
        this.adminGetOrderDetailUseCase = adminGetOrderDetailUseCase;
        this.getOrderStatsTodayUseCase = getOrderStatsTodayUseCase;
        this.shipOrderUseCase = shipOrderUseCase;
        this.adminCancelOrderUseCase = adminCancelOrderUseCase;
        this.closeOrderIfUnpaidUseCase = closeOrderIfUnpaidUseCase;
        this.markOrderPaidUseCase = markOrderPaidUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_order.read')")
    public OrderListResponse list(@RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int pageSize) {
        var result = adminListOrdersUseCase.list(new AdminListOrdersQuery(status, page, pageSize));
        var items = result.items().stream()
                .map(s -> new OrderSummaryResponse(s.id(), s.orderSn(), s.memberId(), s.status(), s.payAmount(),
                        s.paymentMethod(), s.createdAt()))
                .toList();
        return new OrderListResponse(items, result.total());
    }

    @GetMapping("/{orderSn}")
    @PreAuthorize("hasAuthority('SCOPE_order.read')")
    public OrderDetailResponse get(@PathVariable String orderSn) {
        var d = adminGetOrderDetailUseCase.get(orderSn);
        var items = d.items().stream()
                .map(i -> new OrderItemResponse(i.skuId(), i.spuId(), i.skuName(), i.skuImage(), i.price(),
                        i.count(), i.subtotal()))
                .toList();
        return new OrderDetailResponse(d.id(), d.orderSn(), d.memberId(), d.status(), d.cancelReason(),
                d.totalAmount(), d.discountAmount(), d.payAmount(), d.paymentMethod(), d.couponId(),
                d.pointsUsed(), d.pointsDiscountAmount(), d.receiverName(), d.receiverPhone(), d.city(),
                d.district(), d.postalCode(), d.street(), d.remark(), d.receiptTime(), d.createdAt(), items);
    }

    @GetMapping("/stats/today")
    @PreAuthorize("hasAuthority('SCOPE_order.read')")
    public OrderStatsTodayResponse statsToday() {
        var result = getOrderStatsTodayUseCase.get();
        return new OrderStatsTodayResponse(result.newOrderCount());
    }

    @PutMapping("/{orderSn}/ship")
    @PreAuthorize("hasAuthority('SCOPE_order.write')")
    public ResponseEntity<Void> ship(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable String orderSn) {
        shipOrderUseCase.ship(new ShipOrderCommand(operator(identityAssertion), orderSn));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderSn}/cancel")
    @PreAuthorize("hasAuthority('SCOPE_order.write')")
    public ResponseEntity<Void> cancel(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable String orderSn, @Valid @RequestBody AdminCancelOrderRequest request) {
        adminCancelOrderUseCase
                .cancel(new AdminCancelOrderCommand(operator(identityAssertion), orderSn, request.reason()));
        return ResponseEntity.noContent().build();
    }

    /** 供 Phase 7 tengan-payment 逾時通知呼叫；MQ 消費者在同一服務內直接呼叫 use case，不走這支 HTTP 端點。 */
    @PutMapping("/{orderSn}/close-if-unpaid")
    @PreAuthorize("hasAuthority('SCOPE_order.write')")
    public ResponseEntity<Void> closeIfUnpaid(@PathVariable String orderSn) {
        closeOrderIfUnpaidUseCase.close(orderSn);
        return ResponseEntity.noContent().build();
    }

    /** 供 tengan-payment 驗完 ECPay callback／LINE Pay confirm 或 COD 同步完成後呼叫；系統觸發，不是管理員動作，不用 X-Identity-Assertion。 */
    @PutMapping("/{orderSn}/paid")
    @PreAuthorize("hasAuthority('SCOPE_order.write')")
    public ResponseEntity<Void> markPaid(@PathVariable String orderSn) {
        markOrderPaidUseCase.markPaid(orderSn);
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
