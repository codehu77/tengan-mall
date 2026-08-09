package com.tengan.mall.inventory.domain.model;

/** 聚合根：倉庫。扁平結構，比照 Brand——只有新增/列表，沒有修改/刪除需求（見文件 API 清單）。 */
public class WareInfo {

    private Long id;
    private final String name;
    private final String address;

    private WareInfo(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public static WareInfo create(String name, String address) {
        return new WareInfo(null, name, address);
    }

    public static WareInfo reconstitute(Long id, String name, String address) {
        return new WareInfo(id, name, address);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("WareInfo 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
