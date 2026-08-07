package com.tengan.mall.member.application.member;

public interface GetMemberDetailUseCase {

    MemberSummary get(Long memberId);
}
