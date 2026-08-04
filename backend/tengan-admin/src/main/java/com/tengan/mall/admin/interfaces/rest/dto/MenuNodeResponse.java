package com.tengan.mall.admin.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 對齊 pure-admin 動態路由期待的形狀，見 application/menutree/MenuNode 的說明。
 *
 * <p>{@code @JsonInclude(NON_EMPTY)} 是必要的，不是美化輸出：葉節點（沒有子選單的頁面）的
 * {@code children} 一定要在 JSON 裡完全省略，不能序列化成 {@code "children":[]} 或
 * {@code "children":null}。JS 對「欄位不存在」(undefined)、「欄位是 null」、「欄位是空陣列」
 * 是三種不同的值——pure-admin 兩個地方分別踩雷：
 * <ul>
 *   <li>{@code router/utils.ts} 的 {@code filterChildrenTree} 用 {@code v?.children?.length !== 0}
 *       判斷「是不是空容器該隱藏」，空陣列 {@code []} 會被當成「這是個空目錄」整個濾掉，
 *       連父節點都會被牽連濾掉（實測過：整個「系統管理」選單消失，只剩靜態的首頁/異常頁面）。</li>
 *   <li>{@code SidebarItem.vue} 用預設參數 {@code function hasOneShowingChild(children = [], parent)}
 *       接 children，這個預設值只在收到 undefined 時才生效，收到 null 會直接把 null 傳進去，
 *       後面呼叫 {@code children.filter(...)} 就整個炸掉（實測過：登入後畫面整個空白，
 *       console 噴 {@code Cannot read properties of null (reading 'filter')}）。</li>
 * </ul>
 * 只有「完全省略這個欄位」（JS 讀到 undefined）同時滿足兩邊的預期，null 或空陣列都不行。
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MenuNodeResponse(String path, String name, String component, MenuMetaResponse meta,
        List<MenuNodeResponse> children) {
}
