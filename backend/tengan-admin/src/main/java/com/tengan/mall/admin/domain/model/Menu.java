package com.tengan.mall.admin.domain.model;

import com.tengan.mall.admin.domain.exception.InvalidMenuRouteException;
import java.util.Optional;

/**
 * 聚合根。{@code parentId} 為空代表根節點（DB 層用 0 標記，不是實體 FK）。
 * {@code menuType} 建立後不可變更——目錄/選單/按鈕是節點的本質分類，改分類等於是換一個節點。
 */
public class Menu {

    private MenuId id;
    private final MenuId parentId;
    private final MenuType menuType;
    private String title;
    private String path;
    private String component;
    private String routeName;
    private String icon;
    private PermissionCode permissionCode;
    private int sortOrder;
    private MenuStatus status;

    private Menu(MenuId id, MenuId parentId, MenuType menuType, String title, String path, String component,
            String routeName, String icon, PermissionCode permissionCode, int sortOrder, MenuStatus status) {
        this.id = id;
        this.parentId = parentId;
        this.menuType = menuType;
        this.title = title;
        this.path = path;
        this.component = component;
        this.routeName = routeName;
        this.icon = icon;
        this.permissionCode = permissionCode;
        this.sortOrder = sortOrder;
        this.status = status;
    }

    public static Menu create(MenuId parentId, MenuType menuType, String title, String path, String component,
            String routeName, String icon, PermissionCode permissionCode, int sortOrder) {
        validateRoutable(menuType, path, component, routeName);
        return new Menu(null, parentId, menuType, title, path, component, routeName, icon, permissionCode,
                sortOrder, MenuStatus.ACTIVE);
    }

    public static Menu reconstitute(MenuId id, MenuId parentId, MenuType menuType, String title, String path,
            String component, String routeName, String icon, PermissionCode permissionCode, int sortOrder,
            MenuStatus status) {
        return new Menu(id, parentId, menuType, title, path, component, routeName, icon, permissionCode, sortOrder,
                status);
    }

    public void assignId(MenuId id) {
        if (this.id != null) {
            throw new IllegalStateException("Menu 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void update(String title, String path, String component, String routeName, String icon,
            PermissionCode permissionCode, int sortOrder) {
        validateRoutable(menuType, path, component, routeName);
        this.title = title;
        this.path = path;
        this.component = component;
        this.routeName = routeName;
        this.icon = icon;
        this.permissionCode = permissionCode;
        this.sortOrder = sortOrder;
    }

    /**
     * BUTTON 不是路由，不需要這些欄位；CATALOG/MENU 都需要 path 才能在前端組出路由樹；
     * MENU 額外需要 component/routeName，不然 pure-admin 的 addAsyncRoutes() 解析不出對應
     * 頁面元件，會產生一個沒有 name、沒有 path 的殘缺路由，讓 Vue Router 註冊路由時直接丟
     * 例外，把整個後台的動態路由註冊流程打斷——不是只有這個節點壞掉，是全部選單都連帶進不去。
     * 只在 create/update（寫入路徑）驗證，reconstitute（讀取既有資料）刻意不驗證：
     * 萬一資料庫裡已經有不合格的舊資料，還是要能查得出來、刪得掉，不能連查詢都直接炸掉。
     */
    private static void validateRoutable(MenuType menuType, String path, String component, String routeName) {
        if (menuType == MenuType.BUTTON) {
            return;
        }
        if (path == null || path.isBlank()) {
            throw new InvalidMenuRouteException(menuType, "path");
        }
        if (menuType == MenuType.MENU) {
            if (component == null || component.isBlank()) {
                throw new InvalidMenuRouteException(menuType, "component");
            }
            if (routeName == null || routeName.isBlank()) {
                throw new InvalidMenuRouteException(menuType, "routeName");
            }
        }
    }

    public void activate() {
        this.status = MenuStatus.ACTIVE;
    }

    public void disable() {
        this.status = MenuStatus.DISABLED;
    }

    public boolean isButton() {
        return menuType == MenuType.BUTTON;
    }

    public MenuId getId() {
        return id;
    }

    public Optional<MenuId> getParentId() {
        return Optional.ofNullable(parentId);
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getComponent() {
        return component;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getIcon() {
        return icon;
    }

    public Optional<PermissionCode> getPermissionCode() {
        return Optional.ofNullable(permissionCode);
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public MenuStatus getStatus() {
        return status;
    }
}
