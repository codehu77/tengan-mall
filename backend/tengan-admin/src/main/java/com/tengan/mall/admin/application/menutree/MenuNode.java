package com.tengan.mall.admin.application.menutree;

import java.util.List;

/**
 * 對齊 pure-admin {@code handleAsyncRoutes()} 期待的巢狀路由樹形狀。BUTTON 型選單節點不會
 * 出現在這裡（不是路由），它們的 permissionCode 會被摺進父節點的 {@link MenuMeta#auths()}。
 */
public record MenuNode(String path, String name, String component, MenuMeta meta, List<MenuNode> children) {
}
