package com.tengan.mall.admin.application.port;

import java.util.List;

public record MemberListResult(List<MemberItem> items, long total) {
}
