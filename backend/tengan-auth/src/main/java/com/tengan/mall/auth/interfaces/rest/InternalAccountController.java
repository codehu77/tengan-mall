package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.application.account.DisableAccountCommand;
import com.tengan.mall.auth.application.account.DisableAccountUseCase;
import com.tengan.mall.auth.application.account.EnableAccountCommand;
import com.tengan.mall.auth.application.account.EnableAccountUseCase;
import com.tengan.mall.auth.application.account.GetAccountStatusesUseCase;
import com.tengan.mall.auth.interfaces.rest.dto.AccountStatusItemResponse;
import com.tengan.mall.auth.interfaces.rest.dto.AccountStatusListResponse;
import com.tengan.mall.jwt.IdentityAssertionVerifier;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 給 tengan-admin 呼叫。disable/enable 真正把 account.status 改掉——這是 LoginService 實際
 * 檢查的欄位。GET 這支批次查詢給 tengan-admin 的會員列表/詳情頁即時組裝顯示用（見 Member
 * 聚合根 javadoc：member 本身不存這個狀態的複本，每次都問這裡）。
 */
@RestController
@RequestMapping("/internal/accounts")
public class InternalAccountController {

    private final DisableAccountUseCase disableAccountUseCase;
    private final EnableAccountUseCase enableAccountUseCase;
    private final GetAccountStatusesUseCase getAccountStatusesUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalAccountController(DisableAccountUseCase disableAccountUseCase,
            EnableAccountUseCase enableAccountUseCase, GetAccountStatusesUseCase getAccountStatusesUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.disableAccountUseCase = disableAccountUseCase;
        this.enableAccountUseCase = enableAccountUseCase;
        this.getAccountStatusesUseCase = getAccountStatusesUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_account.read')")
    public AccountStatusListResponse statuses(@RequestParam List<Long> ids) {
        var items = getAccountStatusesUseCase.get(ids).stream()
                .map(i -> new AccountStatusItemResponse(i.accountId(), i.status()))
                .toList();
        return new AccountStatusListResponse(items);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('SCOPE_account.write')")
    public ResponseEntity<Void> disable(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        disableAccountUseCase.disable(new DisableAccountCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('SCOPE_account.write')")
    public ResponseEntity<Void> enable(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        enableAccountUseCase.enable(new EnableAccountCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
