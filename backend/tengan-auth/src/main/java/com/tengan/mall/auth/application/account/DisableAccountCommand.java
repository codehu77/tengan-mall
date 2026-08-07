package com.tengan.mall.auth.application.account;

public record DisableAccountCommand(String operator, Long accountId) {
}
