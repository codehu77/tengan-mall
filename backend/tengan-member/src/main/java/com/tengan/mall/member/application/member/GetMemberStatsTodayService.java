package com.tengan.mall.member.application.member;

import com.tengan.mall.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

/** 供 tengan-admin dashboard 組裝今日 KPI 用（見 backend_dev_plan.md 後台儀表板設計）。 */
@Service
public class GetMemberStatsTodayService implements GetMemberStatsTodayUseCase {

    private final MemberRepository memberRepository;

    public GetMemberStatsTodayService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberStatsTodayResult get() {
        return new MemberStatsTodayResult(memberRepository.countCreatedToday());
    }
}
