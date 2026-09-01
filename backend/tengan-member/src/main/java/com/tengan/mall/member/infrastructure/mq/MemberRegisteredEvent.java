package com.tengan.mall.member.infrastructure.mq;

/**
 * 消費端自己開的結構相同、但完全獨立的型別——不共用 tengan-auth 那邊的 MemberRegisteredEvent
 * 類別，兩邊只靠 JSON 欄位名稱對齊（見 RabbitConfig 的 TypePrecedence.INFERRED 說明）。
 * nickname/avatarUrl 只有 OAuth 註冊才有值，密碼註冊是 null（見 tengan-auth 發布端註解）。
 */
public record MemberRegisteredEvent(Long memberId, String phone, String email, String nickname, String avatarUrl) {
}
