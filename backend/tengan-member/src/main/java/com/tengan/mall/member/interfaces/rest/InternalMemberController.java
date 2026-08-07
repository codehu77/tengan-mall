package com.tengan.mall.member.interfaces.rest;

import com.tengan.mall.member.application.member.GetMemberDetailUseCase;
import com.tengan.mall.member.application.member.GetMemberStatsTodayUseCase;
import com.tengan.mall.member.application.member.ListMembersUseCase;
import com.tengan.mall.member.application.member.MemberSummary;
import com.tengan.mall.member.application.member.SearchMembersQuery;
import com.tengan.mall.member.interfaces.rest.dto.MemberListResponse;
import com.tengan.mall.member.interfaces.rest.dto.MemberStatsTodayResponse;
import com.tengan.mall.member.interfaces.rest.dto.MemberSummaryResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 純唯讀——沒有 ban/unban 這種寫入端點。停權/復權是 tengan-auth 的 account.status 才有的概念
 * （見 Member 聚合根的 javadoc），這裡只提供 profile 查詢給 tengan-admin 組列表用。
 */
@RestController
@RequestMapping("/internal/members")
public class InternalMemberController {

    private final ListMembersUseCase listMembersUseCase;
    private final GetMemberDetailUseCase getMemberDetailUseCase;
    private final GetMemberStatsTodayUseCase getMemberStatsTodayUseCase;

    public InternalMemberController(ListMembersUseCase listMembersUseCase,
            GetMemberDetailUseCase getMemberDetailUseCase, GetMemberStatsTodayUseCase getMemberStatsTodayUseCase) {
        this.listMembersUseCase = listMembersUseCase;
        this.getMemberDetailUseCase = getMemberDetailUseCase;
        this.getMemberStatsTodayUseCase = getMemberStatsTodayUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_member.read')")
    public MemberListResponse list(@RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        var result = listMembersUseCase.search(new SearchMembersQuery(keyword, pageNum, pageSize));
        return new MemberListResponse(result.items().stream().map(this::toResponse).toList(), result.total());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_member.read')")
    public MemberSummaryResponse detail(@PathVariable Long id) {
        return toResponse(getMemberDetailUseCase.get(id));
    }

    @GetMapping("/stats/today")
    @PreAuthorize("hasAuthority('SCOPE_member.read')")
    public MemberStatsTodayResponse statsToday() {
        var result = getMemberStatsTodayUseCase.get();
        return new MemberStatsTodayResponse(result.newMemberCount());
    }

    private MemberSummaryResponse toResponse(MemberSummary summary) {
        return new MemberSummaryResponse(summary.id(), summary.username(), summary.phone(), summary.nickname(),
                summary.avatarUrl());
    }
}
