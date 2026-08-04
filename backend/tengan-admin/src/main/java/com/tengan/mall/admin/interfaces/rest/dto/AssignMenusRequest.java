package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record AssignMenusRequest(@NotNull Set<Long> menuIds) {
}
