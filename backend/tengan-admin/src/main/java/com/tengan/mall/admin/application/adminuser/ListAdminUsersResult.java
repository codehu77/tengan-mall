package com.tengan.mall.admin.application.adminuser;

import java.util.List;

public record ListAdminUsersResult(List<AdminUserSummary> items, long total) {
}
