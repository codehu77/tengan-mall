package com.tengan.mall.member.interfaces.rest;

import com.tengan.mall.member.application.member.GetMemberDetailUseCase;
import com.tengan.mall.member.application.member.GetMemberStatsTodayUseCase;
import com.tengan.mall.member.application.member.GetMembersByIdsUseCase;
import com.tengan.mall.member.application.member.ListMembersUseCase;
import com.tengan.mall.member.application.member.MemberSummary;
import com.tengan.mall.member.application.member.SearchMembersQuery;
import com.tengan.mall.member.interfaces.rest.dto.MemberListResponse;
import com.tengan.mall.member.interfaces.rest.dto.MemberStatsTodayResponse;
import com.tengan.mall.member.interfaces.rest.dto.MemberSummaryResponse;
import java.util.List;
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
    private final GetMembersByIdsUseCase getMembersByIdsUseCase;

    public InternalMemberController(ListMembersUseCase listMembersUseCase,
            GetMemberDetailUseCase getMemberDetailUseCase, GetMemberStatsTodayUseCase getMemberStatsTodayUseCase,
            GetMembersByIdsUseCase getMembersByIdsUseCase) {
        this.listMembersUseCase = listMembersUseCase;
        this.getMemberDetailUseCase = getMemberDetailUseCase;
        this.getMemberStatsTodayUseCase = getMemberStatsTodayUseCase;
        this.getMembersByIdsUseCase = getMembersByIdsUseCase;
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

    /** 批次查詢，供 tengan-admin 組付款/訂閱列表的會員帳號/暱稱顯示用，避免逐列各查一次。 */
    @GetMapping("/batch")
    @PreAuthorize("hasAuthority('SCOPE_member.read')")
    public List<MemberSummaryResponse> batch(@RequestParam List<Long> ids) {
        return getMembersByIdsUseCase.get(ids).stream().map(this::toResponse).toList();
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
