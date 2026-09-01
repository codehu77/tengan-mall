package com.tengan.mall.auth.application.port;

/**
 * 電話/Email 變更成功後發布，讓 tengan-member 更新它自己那份唯讀快照（member.phone/email），
 * 見 login_register_redesign「會員自助修改電話/Email」設計——tengan-admin 的買家識別欄查的是
 * tengan-member 這份快照，不同步的話後台會顯示舊資料。
 */
public interface AccountContactChangedEventPublisherPort {

    void publish(Long accountId, String phone, String email);
}
