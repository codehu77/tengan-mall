package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.MemberItem;
import com.tengan.mall.admin.application.port.MemberPort;
import com.tengan.mall.admin.application.port.SubscriptionPort;
import com.tengan.mall.admin.interfaces.rest.dto.SubscriptionPaymentResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SubscriptionRecordListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SubscriptionRecordResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-payment 的 /internal/subscriptions，跟 {@link PaymentController} 同樣的純代理原則，
 * 會員帳號/暱稱同樣即時向 tengan-member 批次組裝。 */
@RestController
@RequestMapping("/api/admin/subscriptions")
public class SubscriptionController {

    private final SubscriptionPort subscriptionPort;
    private final MemberPort memberPort;

    public SubscriptionController(SubscriptionPort subscriptionPort, MemberPort memberPort) {
        this.subscriptionPort = subscriptionPort;
        this.memberPort = memberPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('subscription:list:read')")
    public SubscriptionRecordListResponse list(@RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = subscriptionPort.listSubscriptions(memberId, status, page, pageSize);
        Map<Long, MemberItem> memberById = memberById(result.items().stream().map(r -> r.memberId()).toList());
        var items = result.items().stream()
                .map(r -> {
                    MemberItem member = memberById.get(r.memberId());
                    return new SubscriptionRecordResponse(r.id(), r.memberId(),
                            member == null ? null : member.username(), member == null ? null : member.nickname(),
                            r.targetTier(), r.status(), r.ecpayMerchantTradeNo(), r.periodAmount(),
                            r.consecutiveFailures(), r.paidUntil(), r.benefitExpiredAt(), r.createdAt(),
                            r.cancelledAt());
                })
                .toList();
        return new SubscriptionRecordListResponse(items, result.total());
    }

    private Map<Long, MemberItem> memberById(List<Long> memberIds) {
        List<Long> distinctIds = memberIds.stream().distinct().toList();
        return memberPort.getMembers(distinctIds).stream().collect(Collectors.toMap(MemberItem::id, Function.identity()));
    }

    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('subscription:list:read')")
    public List<SubscriptionPaymentResponse> payments(@PathVariable Long id) {
        return subscriptionPort.listSubscriptionPayments(id).stream()
                .map(p -> new SubscriptionPaymentResponse(p.id(), p.gwsr(), p.success(), p.amount(),
                        p.totalSuccessTimes(), p.processDate(), p.createdAt()))
                .toList();
    }
}
