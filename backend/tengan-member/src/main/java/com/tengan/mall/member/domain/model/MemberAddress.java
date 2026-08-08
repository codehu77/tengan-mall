package com.tengan.mall.member.domain.model;

/**
 * 聚合根：會員收件地址，獨立於 Member 之外（有自己的 CRUD 生命週期，不是 Member 底下的
 * child entity）。地址拆成 city/district/postalCode/street 四個結構化欄位，配合前台
 * 城市+區下拉選單的表單設計（2026-08-08 由單一自由文字欄位改版）。
 */
public class MemberAddress {

    private Long id;
    private final Long memberId;
    private String receiverName;
    private String receiverPhone;
    private String city;
    private String district;
    private String postalCode;
    private String street;
    private boolean isDefault;

    private MemberAddress(Long id, Long memberId, String receiverName, String receiverPhone, String city,
            String district, String postalCode, String street, boolean isDefault) {
        this.id = id;
        this.memberId = memberId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
        this.street = street;
        this.isDefault = isDefault;
    }

    public static MemberAddress create(Long memberId, String receiverName, String receiverPhone, String city,
            String district, String postalCode, String street, boolean isDefault) {
        return new MemberAddress(null, memberId, receiverName, receiverPhone, city, district, postalCode, street,
                isDefault);
    }

    public static MemberAddress reconstitute(Long id, Long memberId, String receiverName, String receiverPhone,
            String city, String district, String postalCode, String street, boolean isDefault) {
        return new MemberAddress(id, memberId, receiverName, receiverPhone, city, district, postalCode, street,
                isDefault);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("MemberAddress 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void update(String receiverName, String receiverPhone, String city, String district, String postalCode,
            String street) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
        this.street = street;
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

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStreet() {
        return street;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
