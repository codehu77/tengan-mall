package com.tengan.mall.admin.application.port;

/**
 * 呼叫 tengan-member 的會員 internal 端點。純唯讀——member 沒有 status/停權概念（那是
 * tengan-auth 的 account.status，見 AccountPort），這裡只轉發 profile 查詢。
 */
public interface MemberPort {

    MemberListResult listMembers(String keyword, int pageNum, int pageSize);

    MemberItem getMember(Long id);
}
