package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.MemberItem;
import com.tengan.mall.admin.application.port.MemberPort;
import com.tengan.mall.admin.application.port.PaymentPort;
import com.tengan.mall.admin.interfaces.rest.dto.PaymentMethodConfigResponse;
import com.tengan.mall.admin.interfaces.rest.dto.PaymentRecordListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.PaymentRecordResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ReconcileNowResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdatePaymentMethodStatusRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

/** BFF：轉發到 tengan-payment 的 /internal/payments，跟 {@link OrderController} 同樣的純代理原則。
 * 會員帳號/暱稱是即時向 tengan-member 批次組裝的（見 [[db_design_conventions]] 跨服務欄位複本判準），
 * 不在 tengan-payment 落地複本——內部低流量管理頁，即時正確優先於省一次網路呼叫。 */
@RestController
@RequestMapping("/api/admin/payments")
public class PaymentController {

    private final PaymentPort paymentPort;
    private final MemberPort memberPort;

    public PaymentController(PaymentPort paymentPort, MemberPort memberPort) {
        this.paymentPort = paymentPort;
        this.memberPort = memberPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('payment:list:read')")
    public PaymentRecordListResponse list(@RequestParam(required = false) String orderSn,
            @RequestParam(required = false) String method, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = paymentPort.listPaymentRecords(orderSn, method, page, pageSize);
        Map<Long, MemberItem> memberById = memberById(result.items().stream().map(r -> r.memberId()).toList());
        var items = result.items().stream()
                .map(r -> {
                    MemberItem member = memberById.get(r.memberId());
                    return new PaymentRecordResponse(r.id(), r.orderSn(), r.memberId(),
                            member == null ? null : member.username(), member == null ? null : member.nickname(),
                            r.method(), r.amount(), r.status(), r.gatewayTradeNo(), r.paidAt(), r.createdAt());
                })
                .toList();
        return new PaymentRecordListResponse(items, result.total());
    }

    private Map<Long, MemberItem> memberById(List<Long> memberIds) {
        List<Long> distinctIds = memberIds.stream().distinct().toList();
        return memberPort.getMembers(distinctIds).stream().collect(Collectors.toMap(MemberItem::id, Function.identity()));
    }

    @GetMapping("/methods")
    @PreAuthorize("hasAuthority('payment:list:read')")
    public List<PaymentMethodConfigResponse> methods() {
        return paymentPort.listPaymentMethods().stream()
                .map(m -> new PaymentMethodConfigResponse(m.method(), m.enabled())).toList();
    }

    @PutMapping("/methods/{method}/status")
    @PreAuthorize("hasAuthority('payment:method:write')")
    public ResponseEntity<Void> updateMethodStatus(@AuthenticationPrincipal Jwt operatorJwt,
            @PathVariable String method, @RequestBody UpdatePaymentMethodStatusRequest request) {
        paymentPort.updatePaymentMethodStatus(method, request.enabled(), operatorJwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reconcile-now")
    @PreAuthorize("hasAuthority('payment:reconcile:write')")
    public ReconcileNowResponse reconcileNow() {
        var result = paymentPort.triggerReconcileNow();
        return new ReconcileNowResponse(result.paymentChecked(), result.paymentConverged(), result.paymentFailed(),
                result.subscriptionChecked(), result.subscriptionConverged(), result.subscriptionFailed(),
                result.renewalChecked(), result.renewalRecovered());
    }
}
