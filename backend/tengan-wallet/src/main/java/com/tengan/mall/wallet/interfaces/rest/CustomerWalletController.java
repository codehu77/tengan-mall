package com.tengan.mall.wallet.interfaces.rest;

import com.tengan.mall.wallet.application.points.GetExpiringBatchesUseCase;
import com.tengan.mall.wallet.application.points.GetPointsSummaryUseCase;
import com.tengan.mall.wallet.application.points.GetPointsTransactionDetailUseCase;
import com.tengan.mall.wallet.application.points.GetTransactionCountsUseCase;
import com.tengan.mall.wallet.application.points.ListPointsTransactionsQuery;
import com.tengan.mall.wallet.application.points.ListPointsTransactionsUseCase;
import com.tengan.mall.wallet.application.points.PreviewRedeemCommand;
import com.tengan.mall.wallet.application.points.PreviewRedeemUseCase;
import com.tengan.mall.wallet.application.points.PointsTransactionView;
import com.tengan.mall.wallet.application.tier.GetMemberTierUseCase;
import com.tengan.mall.wallet.domain.model.PointsTransactionStatus;
import com.tengan.mall.wallet.domain.model.PointsTransactionType;
import com.tengan.mall.wallet.interfaces.rest.dto.ExpiringBatchesResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.MemberTierResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.PointBatchResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.PointsSummaryResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.PointsTransactionListResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.PointsTransactionResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.RedeemPreviewRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.RedeemPreviewResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.TransactionCountsResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** zero-trust：直接用 userJwtDecoder 驗證的 Jwt 取 sub 當 memberId，不信任 Gateway 轉發的任何明文身份資訊。 */
@RestController
@RequestMapping("/api/customer/wallet/points")
public class CustomerWalletController {

    private final GetPointsSummaryUseCase getPointsSummaryUseCase;
    private final GetMemberTierUseCase getMemberTierUseCase;
    private final GetExpiringBatchesUseCase getExpiringBatchesUseCase;
    private final ListPointsTransactionsUseCase listPointsTransactionsUseCase;
    private final GetPointsTransactionDetailUseCase getPointsTransactionDetailUseCase;
    private final GetTransactionCountsUseCase getTransactionCountsUseCase;
    private final PreviewRedeemUseCase previewRedeemUseCase;

    public CustomerWalletController(GetPointsSummaryUseCase getPointsSummaryUseCase,
            GetMemberTierUseCase getMemberTierUseCase, GetExpiringBatchesUseCase getExpiringBatchesUseCase,
            ListPointsTransactionsUseCase listPointsTransactionsUseCase,
            GetPointsTransactionDetailUseCase getPointsTransactionDetailUseCase,
            GetTransactionCountsUseCase getTransactionCountsUseCase, PreviewRedeemUseCase previewRedeemUseCase) {
        this.getPointsSummaryUseCase = getPointsSummaryUseCase;
        this.getMemberTierUseCase = getMemberTierUseCase;
        this.getExpiringBatchesUseCase = getExpiringBatchesUseCase;
        this.listPointsTransactionsUseCase = listPointsTransactionsUseCase;
        this.getPointsTransactionDetailUseCase = getPointsTransactionDetailUseCase;
        this.getTransactionCountsUseCase = getTransactionCountsUseCase;
        this.previewRedeemUseCase = previewRedeemUseCase;
    }

    @GetMapping("/summary")
    public PointsSummaryResponse summary(@AuthenticationPrincipal Jwt jwt) {
        var r = getPointsSummaryUseCase.get(memberId(jwt));
        return new PointsSummaryResponse(r.availablePoints(), r.pendingPoints(), r.expiringPoints(),
                r.expiringWithinDays(), r.pointValueRatio());
    }

    @GetMapping("/tier")
    public MemberTierResponse tier(@AuthenticationPrincipal Jwt jwt) {
        var r = getMemberTierUseCase.get(memberId(jwt));
        return new MemberTierResponse(r.tier(), r.label(), r.cashbackRate(), r.monthlyCap(), r.monthlyEarnedPoints());
    }

    @GetMapping("/expiring")
    public ExpiringBatchesResponse expiring(@AuthenticationPrincipal Jwt jwt) {
        var items = getExpiringBatchesUseCase.get(memberId(jwt)).stream()
                .map(b -> new PointBatchResponse(b.id(), b.points(), b.earnedAt(), b.expiresAt(), b.sourceOrderSn()))
                .toList();
        return new ExpiringBatchesResponse(items);
    }

    @GetMapping("/transactions")
    public PointsTransactionListResponse transactions(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String type, @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "ALL") String dateRange, @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Instant fromDate = toFromDate(dateRange);
        var result = listPointsTransactionsUseCase.list(new ListPointsTransactionsQuery(memberId(jwt),
                toTypeCode(type), toStatusCode(status), fromDate, keyword, page, pageSize));
        var items = result.items().stream().map(this::toResponse).toList();
        return new PointsTransactionListResponse(items, result.total());
    }

    /** 各 (type, status) 組合的筆數，交易明細分類 tabs 顯示筆數用（分類定義只活在前端 filter bar 一個地方）。 */
    @GetMapping("/transactions/counts")
    public TransactionCountsResponse transactionCounts(@AuthenticationPrincipal Jwt jwt) {
        var items = getTransactionCountsUseCase.get(memberId(jwt)).stream()
                .map(g -> new TransactionCountsResponse.Item(g.type(), g.status(), g.count()))
                .toList();
        return new TransactionCountsResponse(items);
    }

    @GetMapping("/transactions/{id}")
    public PointsTransactionResponse transactionDetail(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return toResponse(getPointsTransactionDetailUseCase.get(memberId(jwt), id));
    }

    @PostMapping("/redeem")
    public RedeemPreviewResponse redeem(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RedeemPreviewRequest request) {
        var result = previewRedeemUseCase
                .preview(new PreviewRedeemCommand(memberId(jwt), request.orderAmount(), request.points()));
        return new RedeemPreviewResponse(result.valid(), result.discountAmount());
    }

    private PointsTransactionResponse toResponse(PointsTransactionView v) {
        return new PointsTransactionResponse(v.id(), v.type(), v.status(), v.points(), v.balanceAfter(), v.title(),
                v.description(), v.orderSn(), v.channel(), v.operator(), v.createdAt(), v.expiresAt());
    }

    /**
     * type（為什麼變動：EARN/REDEEM/EXPIRE/ADJUST）跟 status（現在算不算數：PENDING/CONFIRMED/
     * REVERSED）是兩個獨立的篩選維度，前端也是兩個獨立的 UI 元件（type 是次要下拉篩選，status 是
     * 主要生命週期分頁），不再像先前那樣把兩者硬塞進同一個參數做字串比對轉譯。
     */
    private Integer toTypeCode(String type) {
        if (type == null || type.isBlank() || "ALL".equalsIgnoreCase(type)) {
            return null;
        }
        try {
            return PointsTransactionType.valueOf(type.toUpperCase()).getValue();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Integer toStatusCode(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return null;
        }
        try {
            return PointsTransactionStatus.valueOf(status.toUpperCase()).getValue();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Instant toFromDate(String dateRange) {
        long days = switch (dateRange == null ? "ALL" : dateRange.toUpperCase()) {
            case "30D" -> 30;
            case "90D" -> 90;
            case "1Y" -> 365;
            default -> -1;
        };
        return days < 0 ? null : Instant.now().minusSeconds(days * 24 * 3600);
    }

    private Long memberId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
