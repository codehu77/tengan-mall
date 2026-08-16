package com.tengan.mall.wallet.interfaces.rest;

import com.tengan.mall.wallet.application.points.AdjustPointsCommand;
import com.tengan.mall.wallet.application.points.AdjustPointsUseCase;
import com.tengan.mall.wallet.application.points.ConsumePointsCommand;
import com.tengan.mall.wallet.application.points.ConsumePointsUseCase;
import com.tengan.mall.wallet.application.points.EarnPointsCommand;
import com.tengan.mall.wallet.application.points.EarnPointsUseCase;
import com.tengan.mall.wallet.application.points.ReservePointsCommand;
import com.tengan.mall.wallet.application.points.ReservePointsUseCase;
import com.tengan.mall.wallet.application.points.RevertPointsCommand;
import com.tengan.mall.wallet.application.points.RevertPointsUseCase;
import com.tengan.mall.wallet.application.rule.GetWalletRuleUseCase;
import com.tengan.mall.wallet.application.rule.UpdateWalletRuleCommand;
import com.tengan.mall.wallet.application.rule.UpdateWalletRuleUseCase;
import com.tengan.mall.wallet.application.tier.UpdateMemberTierCommand;
import com.tengan.mall.wallet.application.tier.UpdateMemberTierUseCase;
import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import com.tengan.mall.wallet.interfaces.rest.dto.AdjustPointsRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.ConsumeRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.ConsumeResponse;
import com.tengan.mall.wallet.interfaces.rest.dto.EarnRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.ReserveRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.RevertRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.UpdateTierRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.UpdateWalletRuleRequest;
import com.tengan.mall.wallet.interfaces.rest.dto.WalletRuleResponse;
import com.tengan.mall.jwt.IdentityAssertionVerifier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/wallet")
public class InternalWalletController {

    private final ReservePointsUseCase reservePointsUseCase;
    private final EarnPointsUseCase earnPointsUseCase;
    private final ConsumePointsUseCase consumePointsUseCase;
    private final RevertPointsUseCase revertPointsUseCase;
    private final AdjustPointsUseCase adjustPointsUseCase;
    private final UpdateMemberTierUseCase updateMemberTierUseCase;
    private final GetWalletRuleUseCase getWalletRuleUseCase;
    private final UpdateWalletRuleUseCase updateWalletRuleUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalWalletController(ReservePointsUseCase reservePointsUseCase, EarnPointsUseCase earnPointsUseCase,
            ConsumePointsUseCase consumePointsUseCase, RevertPointsUseCase revertPointsUseCase,
            AdjustPointsUseCase adjustPointsUseCase, UpdateMemberTierUseCase updateMemberTierUseCase,
            GetWalletRuleUseCase getWalletRuleUseCase, UpdateWalletRuleUseCase updateWalletRuleUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.reservePointsUseCase = reservePointsUseCase;
        this.earnPointsUseCase = earnPointsUseCase;
        this.consumePointsUseCase = consumePointsUseCase;
        this.revertPointsUseCase = revertPointsUseCase;
        this.adjustPointsUseCase = adjustPointsUseCase;
        this.updateMemberTierUseCase = updateMemberTierUseCase;
        this.getWalletRuleUseCase = getWalletRuleUseCase;
        this.updateWalletRuleUseCase = updateWalletRuleUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    /** 供 tengan-order 的 ConfirmReceiptService 非關鍵路徑呼叫（safely() 包裹）。 */
    @PostMapping("/points/reserve")
    @PreAuthorize("hasAuthority('SCOPE_wallet.write')")
    public ResponseEntity<Void> reserve(@Valid @RequestBody ReserveRequest request) {
        reservePointsUseCase
                .reserve(new ReservePointsCommand(request.memberId(), request.orderSn(), request.payAmount()));
        return ResponseEntity.noContent().build();
    }

    /** 供 tengan-order 的 PointsGrantScheduler（鑑賞期過後）呼叫。 */
    @PostMapping("/points/earn")
    @PreAuthorize("hasAuthority('SCOPE_wallet.write')")
    public ResponseEntity<Void> earn(@Valid @RequestBody EarnRequest request) {
        earnPointsUseCase.earn(new EarnPointsCommand(request.memberId(), request.orderSn(), request.payAmount()));
        return ResponseEntity.noContent().build();
    }

    /** 供 tengan-order 下單 Saga 呼叫。 */
    @PostMapping("/points/consume")
    @PreAuthorize("hasAuthority('SCOPE_wallet.write')")
    public ConsumeResponse consume(@Valid @RequestBody ConsumeRequest request) {
        var result = consumePointsUseCase
                .consume(new ConsumePointsCommand(request.memberId(), request.points(), request.orderSn()));
        return new ConsumeResponse(result.discountAmount());
    }

    /** 供 tengan-order 取消訂單三條路徑（顧客取消/逾時取消/客服代取消）呼叫，冪等 no-op 安全。 */
    @PostMapping("/points/revert")
    @PreAuthorize("hasAuthority('SCOPE_wallet.write')")
    public ResponseEntity<Void> revert(@Valid @RequestBody RevertRequest request) {
        revertPointsUseCase.revert(new RevertPointsCommand(request.memberId(), request.orderSn()));
        return ResponseEntity.noContent().build();
    }

    /** 供 tengan-admin 呼叫，對應「客服補償/扣回點數」情境。 */
    @PostMapping("/points/adjust")
    @PreAuthorize("hasAuthority('SCOPE_wallet.write')")
    public ResponseEntity<Void> adjust(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody AdjustPointsRequest request) {
        adjustPointsUseCase.adjust(new AdjustPointsCommand(request.memberId(), request.points(), request.reason(),
                operator(identityAssertion)));
        return ResponseEntity.noContent().build();
    }

    /** Phase 8.5（訂閱）上線前唯一的升等入口，供 tengan-admin 呼叫。 */
    @PostMapping("/members/{memberId}/tier")
    @PreAuthorize("hasAuthority('SCOPE_wallet.write')")
    public ResponseEntity<Void> updateTier(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateTierRequest request) {
        updateMemberTierUseCase.update(new UpdateMemberTierCommand(memberId,
                MemberTierLevel.valueOf(request.tier().toUpperCase()), request.reason(), operator(identityAssertion)));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules")
    @PreAuthorize("hasAuthority('SCOPE_wallet.read')")
    public WalletRuleResponse getRules() {
        var r = getWalletRuleUseCase.get();
        return new WalletRuleResponse(r.cashbackRatePro(), r.cashbackRateProPlus(), r.monthlyCapPro(),
                r.monthlyCapProPlus(), r.pointExpiryDays(), r.gracePeriodMinutes(), r.pointValueRatio());
    }

    @PutMapping("/rules")
    @PreAuthorize("hasAuthority('SCOPE_wallet.write')")
    public ResponseEntity<Void> updateRules(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody UpdateWalletRuleRequest request) {
        operator(identityAssertion);
        updateWalletRuleUseCase.update(new UpdateWalletRuleCommand(request.cashbackRatePro(),
                request.cashbackRateProPlus(), request.monthlyCapPro(), request.monthlyCapProPlus(),
                request.pointExpiryDays(), request.gracePeriodMinutes(), request.pointValueRatio()));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
