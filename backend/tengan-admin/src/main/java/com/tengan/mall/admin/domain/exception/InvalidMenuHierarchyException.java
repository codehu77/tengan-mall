package com.tengan.mall.admin.domain.exception;

import com.tengan.mall.admin.domain.model.MenuType;

/**
 * 目錄底下只能放選單、選單底下只能放按鈕、按鈕底下不能再放任何東西——這是選單樹的階層語意
 * 本身固定如此，不是 Vue Router 技術上做不到（pure-admin 前端的 formatFlatteningRoutes
 * 其實有處理 3 層以上路由攤平的邏輯，技術上是允許更深巢狀的），是這個系統的選單設計本來就
 * 不該允許目錄裡塞目錄、選單裡塞選單，資料一旦長歪，後台選單管理頁跟 pure-admin 的動態路由
 * 樹兩邊的呈現方式就會對不上。
 */
public class InvalidMenuHierarchyException extends RuntimeException {

    public InvalidMenuHierarchyException(MenuType parentType, MenuType childType) {
        super((parentType == null ? "頂層節點" : parentType + " 型節點") + "底下不能放 " + childType + " 型節點");
    }
}
