package com.tengan.mall.member.application.member;

import com.tengan.mall.member.domain.exception.MemberNotFoundException;
import com.tengan.mall.member.domain.model.Member;
import com.tengan.mall.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateProfileService implements UpdateProfileUseCase {

    private final MemberRepository memberRepository;

    public UpdateProfileService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void update(UpdateProfileCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberNotFoundException(command.memberId()));
        member.updateProfile(command.nickname(), command.avatarUrl());
        memberRepository.save(member);
    }
}
