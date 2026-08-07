package com.tengan.mall.member.application.member;

public record SearchMembersQuery(String keyword, int pageNum, int pageSize) {
}
