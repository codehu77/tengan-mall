package com.tengan.mall.member.application.member;

import java.util.List;

public interface GetMembersByIdsUseCase {

    List<MemberSummary> get(List<Long> ids);
}
