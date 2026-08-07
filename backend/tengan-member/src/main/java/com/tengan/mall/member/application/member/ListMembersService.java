package com.tengan.mall.member.application.member;

import com.tengan.mall.member.domain.repository.MemberPage;
import com.tengan.mall.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class ListMembersService implements ListMembersUseCase {

    private final MemberRepository memberRepository;

    public ListMembersService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public ListMembersResult search(SearchMembersQuery query) {
        MemberPage page = memberRepository.search(query.keyword(), query.pageNum(), query.pageSize());
        var items = page.items().stream()
                .map(m -> new MemberSummary(m.getId(), m.getUsername(), m.getPhone(), m.getNickname(),
                        m.getAvatarUrl()))
                .toList();
        return new ListMembersResult(items, page.total());
    }
}
