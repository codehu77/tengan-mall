package com.tengan.mall.member.application.member;

import com.tengan.mall.member.domain.exception.MemberNotFoundException;
import com.tengan.mall.member.domain.model.Member;
import com.tengan.mall.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class GetMemberDetailService implements GetMemberDetailUseCase {

    private final MemberRepository memberRepository;

    public GetMemberDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberSummary get(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return new MemberSummary(member.getId(), member.getUsername(), member.getPhone(), member.getNickname(),
                member.getAvatarUrl());
    }
}
