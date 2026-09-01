package com.tengan.mall.member.domain.model;

/**
 * 聚合根：會員 profile。id 沿用 tengan-auth account 表的 accountId（消費 member.registered
 * 事件時原樣帶入，不是自增）——這是這個服務跟其他聚合根（Category/Brand 等 id 由 DB 自增）唯一不同
 * 的地方，見 create() 直接收 id 參數。
 *
 * <p>暱稱/大頭貼可改，phone/email 是 tengan-auth 那邊的唯讀快照——會員自助改電話/Email 時
 * tengan-auth 發 account.contact_changed 事件回頭同步這裡（見 updateContact()），密碼變更則
 * 不需要同步（這裡從來不存密碼）。等級/會員權益屬於 Wallet
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
    private String phone;
    private String email;
    private String nickname;
    private String avatarUrl;

    private Member(Long id, String phone, String email, String nickname, String avatarUrl) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    /**
     * 消費 member.registered 事件時呼叫。account 不再有 username，暱稱改用通用預設值
     * "會員" + accountId（唯一、使用者之後可在會員中心自行修改），大頭貼留空——不再塞
     * DiceBear 之類的預設圖，前端沒有頭像時自行顯示 placeholder icon。
     */
    public static Member create(Long id, String phone, String email) {
        return create(id, phone, email, null, null);
    }

    /**
     * OAuth 註冊用（Phase 13）：nickname/avatarUrl 有值就用 provider 回傳的個人資料寫入一次
     * （見 oauth_login_design 定案「個人資料只在新建帳號當下同步一次」），空白/null 就跟密碼
     * 註冊一樣留空，兩條路徑共用同一個工廠方法，不重複判斷邏輯。
     */
    public static Member create(Long id, String phone, String email, String nicknameOverride,
            String avatarUrlOverride) {
        String nickname = (nicknameOverride != null && !nicknameOverride.isBlank())
                ? nicknameOverride : "會員" + id;
        String avatarUrl = (avatarUrlOverride != null && !avatarUrlOverride.isBlank())
                ? avatarUrlOverride : null;
        return new Member(id, phone, email, nickname, avatarUrl);
    }

    public static Member reconstitute(Long id, String phone, String email, String nickname, String avatarUrl) {
        return new Member(id, phone, email, nickname, avatarUrl);
    }

    public void updateProfile(String nickname, String avatarUrl) {
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    /** 消費 tengan-auth 的 account.contact_changed 事件時呼叫，更新這份唯讀快照。 */
    public void updateContact(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
