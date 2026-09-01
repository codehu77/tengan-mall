package com.tengan.mall.member.infrastructure.mq;

/** 消費端自己開的獨立型別，跟 tengan-auth 那邊只靠 JSON 欄位名稱對齊（見 MemberRegisteredEvent 的既有模式）。 */
public record AccountContactChangedEvent(Long accountId, String phone, String email) {
}
