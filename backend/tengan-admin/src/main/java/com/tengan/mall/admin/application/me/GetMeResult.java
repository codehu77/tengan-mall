package com.tengan.mall.admin.application.me;

import java.util.List;

public record GetMeResult(Long adminId, String username, String realName, List<String> roleCodes) {
}
