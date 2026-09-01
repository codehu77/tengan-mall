package com.tengan.mall.member.application.member;

public record UpdateContactFromEventCommand(Long memberId, String phone, String email) {
}
