package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.AccountPort;
import com.tengan.mall.admin.application.port.AccountStatusItem;
import com.tengan.mall.admin.application.port.MemberItem;
import com.tengan.mall.admin.application.port.MemberPort;
import com.tengan.mall.admin.interfaces.rest.dto.MemberListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.MemberSummaryResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * BFF：轉發到 tengan-member 的 /internal/members（純 profile），不重做業務規則。
 *
 * <p>狀態（是否停權）不存在 tengan-member，是即時向 tengan-auth 問來即時組裝的——member.id
 * 跟 tengan-auth 的 accountId 是同一個值，所以直接拿 member 的 id 去問 account 狀態。
 * ban/unban 也只呼叫 AccountPort：真正有效果的地方只有 account.status，tengan-member
 * 完全不需要知道「停權」這件事（見 Member 聚合根 javadoc 的設計討論）。</p>
 */
@RestController
@RequestMapping("/api/admin/members")
public class MemberController {

    private final MemberPort memberPort;
    private final AccountPort accountPort;

    public MemberController(MemberPort memberPort, AccountPort accountPort) {
        this.memberPort = memberPort;
        this.accountPort = accountPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('member:read')")
    public MemberListResponse list(@RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        var result = memberPort.listMembers(keyword, pageNum, pageSize);
        Map<Long, Integer> statusById = statusById(result.items().stream().map(MemberItem::id).toList());
        var items = result.items().stream().map(m -> toResponse(m, statusById)).toList();
        return new MemberListResponse(items, result.total());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('member:read')")
    public MemberSummaryResponse detail(@PathVariable Long id) {
        var m = memberPort.getMember(id);
        return toResponse(m, statusById(List.of(id)));
    }

    @PutMapping("/{id}/ban")
    @PreAuthorize("hasAuthority('member:write')")
    public void ban(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        accountPort.disableAccount(id, operatorJwt.getTokenValue());
    }

    @PutMapping("/{id}/unban")
    @PreAuthorize("hasAuthority('member:write')")
    public void unban(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        accountPort.enableAccount(id, operatorJwt.getTokenValue());
    }

    private Map<Long, Integer> statusById(List<Long> ids) {
        return accountPort.getStatuses(ids).stream()
                .collect(Collectors.toMap(AccountStatusItem::accountId, AccountStatusItem::status));
    }

    private MemberSummaryResponse toResponse(MemberItem m, Map<Long, Integer> statusById) {
        // 理論上每個 member.id 在 tengan-auth 都該有對應帳號；萬一查不到（時序上的邊角案例），
        // 前端顯示成「正常」比顯示成一個未定義的狀態碼更安全，不擋整頁渲染。
        int status = statusById.getOrDefault(m.id(), 1);
        return new MemberSummaryResponse(m.id(), m.username(), m.phone(), m.nickname(), m.avatarUrl(), status);
    }
}
