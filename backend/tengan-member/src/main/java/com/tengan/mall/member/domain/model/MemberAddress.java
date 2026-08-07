package com.tengan.mall.member.domain.model;

/**
 * 聚合根：會員收件地址，獨立於 Member 之外（有自己的 CRUD 生命週期，不是 Member 底下的
 * child entity）。地址格式維持單一自由文字欄位（比照 tengan-mall-web 既有 mock 的
 * receiverName/receiverPhone/receiverAddress 形狀），不拆縣市/鄉鎮區結構化欄位。
 */
public class MemberAddress {

    private Long id;
    private final Long memberId;
    private String receiverName;
    private String receiverPhone;
    private String address;
    private boolean isDefault;

    private MemberAddress(Long id, Long memberId, String receiverName, String receiverPhone, String address,
            boolean isDefault) {
        this.id = id;
        this.memberId = memberId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.isDefault = isDefault;
    }

    public static MemberAddress create(Long memberId, String receiverName, String receiverPhone, String address,
            boolean isDefault) {
        return new MemberAddress(null, memberId, receiverName, receiverPhone, address, isDefault);
    }

    public static MemberAddress reconstitute(Long id, Long memberId, String receiverName, String receiverPhone,
            String address, boolean isDefault) {
        return new MemberAddress(id, memberId, receiverName, receiverPhone, address, isDefault);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("MemberAddress 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void update(String receiverName, String receiverPhone, String address) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
    }

    public void markAsDefault() {
        this.isDefault = true;
    }

    public void unmarkAsDefault() {
        this.isDefault = false;
    }

    public boolean belongsTo(Long memberId) {
        return this.memberId.equals(memberId);
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getAddress() {
        return address;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
