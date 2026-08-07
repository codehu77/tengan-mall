package com.tengan.mall.member.application.member;

import java.util.List;

public record ListMembersResult(List<MemberSummary> items, long total) {
}
