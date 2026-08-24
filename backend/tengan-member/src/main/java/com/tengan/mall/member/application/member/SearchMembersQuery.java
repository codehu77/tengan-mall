package com.tengan.mall.member.application.member;

import java.time.Instant;

public record SearchMembersQuery(String keyword, Instant createdFrom, Instant createdTo, int pageNum, int pageSize) {
}
