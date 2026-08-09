package com.tengan.mall.admin.application.me;

import java.util.List;

public record GetMeResult(Long adminId, String username, String realName, String avatarUrl,
        List<String> roleCodes) {
}
