package com.tengan.mall.admin.application.port;

import java.util.List;

/**
 * 呼叫 tengan-member 的會員 internal 端點。純唯讀——member 沒有 status/停權概念（那是
 * tengan-auth 的 account.status，見 AccountPort），這裡只轉發 profile 查詢。
 */
public interface MemberPort {

    MemberListResult listMembers(String keyword, int pageNum, int pageSize);

    MemberItem getMember(Long id);

    /** 供付款/訂閱列表批次組裝顯示會員帳號/暱稱用，比照 AccountPort.getStatuses 同樣的即時組裝模式。 */
    List<MemberItem> getMembers(List<Long> ids);
}
