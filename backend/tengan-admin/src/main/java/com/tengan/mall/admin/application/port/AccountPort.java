package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-auth 的帳號 internal 端點，真正停用/啟用登入用的 account.status。 */
public interface AccountPort {

    void disableAccount(Long accountId, String operatorToken);

    void enableAccount(Long accountId, String operatorToken);

    /** 供會員列表/詳情頁即時組裝顯示用（member 本身不存這個狀態的複本）。 */
    List<AccountStatusItem> getStatuses(List<Long> accountIds);
}
