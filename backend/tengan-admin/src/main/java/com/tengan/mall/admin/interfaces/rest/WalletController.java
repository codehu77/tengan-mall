package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.WalletPort;
import com.tengan.mall.admin.application.port.WalletRuleItem;
import com.tengan.mall.admin.interfaces.rest.dto.AdjustPointsRequest;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateTierRequest;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateWalletRuleRequest;
import com.tengan.mall.admin.interfaces.rest.dto.WalletRuleResponse;
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
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-wallet 的 /internal/wallet/**，跟 {@link PaymentController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/wallet")
public class WalletController {

    private final WalletPort walletPort;

    public WalletController(WalletPort walletPort) {
        this.walletPort = walletPort;
    }

    @GetMapping("/rules")
    @PreAuthorize("hasAuthority('wallet:rules:read')")
    public WalletRuleResponse getRules() {
        var r = walletPort.getRules();
        return new WalletRuleResponse(r.cashbackRatePro(), r.cashbackRateProPlus(), r.monthlyCapPro(),
                r.monthlyCapProPlus(), r.pointExpiryDays(), r.gracePeriodMinutes(), r.pointValueRatio());
    }

    @PutMapping("/rules")
    @PreAuthorize("hasAuthority('wallet:rules:write')")
    public ResponseEntity<Void> updateRules(@AuthenticationPrincipal Jwt operatorJwt,
            @RequestBody UpdateWalletRuleRequest request) {
        walletPort.updateRules(
                new WalletRuleItem(request.cashbackRatePro(), request.cashbackRateProPlus(),
                        request.monthlyCapPro(), request.monthlyCapProPlus(), request.pointExpiryDays(),
                        request.gracePeriodMinutes(), request.pointValueRatio()),
                operatorJwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/members/{memberId}/points/adjust")
    @PreAuthorize("hasAuthority('wallet:adjust:write')")
    public ResponseEntity<Void> adjustPoints(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long memberId,
            @RequestBody AdjustPointsRequest request) {
        walletPort.adjustPoints(memberId, request.points(), request.reason(), operatorJwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/members/{memberId}/tier")
    @PreAuthorize("hasAuthority('wallet:tier:write')")
    public ResponseEntity<Void> updateTier(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long memberId,
            @RequestBody UpdateTierRequest request) {
        walletPort.updateTier(memberId, request.tier(), request.reason(), operatorJwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }
}
