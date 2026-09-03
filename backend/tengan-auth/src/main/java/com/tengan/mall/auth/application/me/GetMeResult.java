package com.tengan.mall.auth.application.me;

public record GetMeResult(Long accountId, String phone, String email, boolean googleLinked, boolean facebookLinked) {
}
