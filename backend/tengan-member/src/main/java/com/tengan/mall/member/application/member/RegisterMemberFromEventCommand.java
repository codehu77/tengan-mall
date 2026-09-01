package com.tengan.mall.member.application.member;

public record RegisterMemberFromEventCommand(Long memberId, String phone, String email) {
}
