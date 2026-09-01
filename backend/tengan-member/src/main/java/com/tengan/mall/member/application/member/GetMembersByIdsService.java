package com.tengan.mall.member.application.member;

import com.tengan.mall.member.domain.repository.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetMembersByIdsService implements GetMembersByIdsUseCase {

    private final MemberRepository memberRepository;

    public GetMembersByIdsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<MemberSummary> get(List<Long> ids) {
        return memberRepository.findByIds(ids).stream()
                .map(m -> new MemberSummary(m.getId(), m.getPhone(), m.getEmail(), m.getNickname(),
                        m.getAvatarUrl()))
                .toList();
    }
}
