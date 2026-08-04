package com.tengan.mall.admin.domain.exception;

public class MenuHasChildrenException extends RuntimeException {

    public MenuHasChildrenException(Long menuId) {
        super("選單底下還有子節點，無法刪除: " + menuId);
    }
}
