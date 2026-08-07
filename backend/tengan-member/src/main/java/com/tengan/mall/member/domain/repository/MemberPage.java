package com.tengan.mall.member.domain.repository;

import com.tengan.mall.member.domain.model.Member;
import java.util.List;

public record MemberPage(List<Member> items, long total) {
}
