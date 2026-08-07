package com.tengan.mall.member.domain.model;

/**
 * 聚合根：會員 profile。id 沿用 tengan-auth account 表的 accountId（消費 member.registered
 * 事件時原樣帶入，不是自增）——這是這個服務跟其他聚合根（Category/Brand 等 id 由 DB 自增）唯一不同
 * 的地方，見 create() 直接收 id 參數。
 *
 * <p>暱稱/大頭貼可改，username/phone 是 tengan-auth 那邊的唯讀快照（帳密變更目前不會回頭同步，
 * 這個專案的帳號系統還沒有改密碼/改手機的流程，暫不處理）。等級/會員權益屬於 Wallet
 * （見 backend_dev_plan.md wallet_rule 設計），不在這個聚合根範圍內。</p>
 *
 * <p><b>刻意沒有 status/停權概念</b>——一度加過 MemberStatus + ban()/unban()，後來確認這跟
 * tengan-auth 的 account.status 是同一件事被存了兩份（member.status 從沒有被任何業務規則讀取，
 * 純粹是顯示用複本），拿掉了。真正擋登入的 account.status 只存在 tengan-auth；後台管理頁需要
 * 顯示/切換「停權」狀態時，由 tengan-admin 這個 BFF 即時去問 tengan-auth 組出來，不在這裡存
 * 第二份複本（見 2026-08 「member 停權」設計討論）。</p>
 */
public class Member {

    private final Long id;
    private final String username;
    private String phone;
    private String nickname;
    private String avatarUrl;

    private Member(Long id, String username, String phone, String nickname, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    /** 消費 member.registered 事件時呼叫，暱稱預設=username，大頭貼給預設 placeholder。 */
    public static Member create(Long id, String username, String phone) {
        return new Member(id, username, phone, username, DefaultAvatar.URL);
    }

    public static Member reconstitute(Long id, String username, String phone, String nickname, String avatarUrl) {
        return new Member(id, username, phone, nickname, avatarUrl);
    }

    public void updateProfile(String nickname, String avatarUrl) {
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    private static final class DefaultAvatar {
        private static final String URL = "https://api.dicebear.com/7.x/identicon/svg?seed=tengan-mall";
    }
}
