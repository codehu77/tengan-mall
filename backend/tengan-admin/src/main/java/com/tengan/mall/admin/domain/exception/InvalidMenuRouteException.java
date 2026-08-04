package com.tengan.mall.admin.domain.exception;

import com.tengan.mall.admin.domain.model.MenuType;

/**
 * 目錄/選單型節點如果缺少 path（目錄+選單都要）或 component/routeName（選單額外要），
 * pure-admin 前端的 addAsyncRoutes() 會解析出一個沒有 name、沒有 path 的殘缺路由，
 * Vue Router 註冊路由時直接丟例外，把整個後台的動態路由註冊流程打斷——不是只有這個節點
 * 壞掉，是全部選單都連帶進不去。這個例外擋在存檔之前，不讓這種資料進得了資料庫。
 */
public class InvalidMenuRouteException extends RuntimeException {

    public InvalidMenuRouteException(MenuType menuType, String missingField) {
        super(menuType + " 型節點缺少必填欄位 " + missingField + "，會讓前端路由註冊整個壞掉");
    }
}
