package com.tengan.mall.member.interfaces.rest.dto;

import java.util.List;

public record MemberListResponse(List<MemberSummaryResponse> items, long total) {
}
