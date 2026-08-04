package com.tengan.mall.admin.domain.repository;

import com.tengan.mall.admin.domain.model.Menu;
import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.RoleId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MenuRepository {

    Menu save(Menu menu);

    Optional<Menu> findById(MenuId id);

    /** 選單表資料量小，管理頁面/選單樹都直接載入全部，不做分頁。 */
    List<Menu> findAll();

    void deleteById(MenuId id);

    boolean existsByParentId(MenuId parentId);

    /**
     * 這組角色被授權的選單 id（只查 role_menu 單表，不 JOIN menu），登入算 permissions claim
     * 跟組選單樹兩個用途共用同一個讀取入口，篩選/組樹的邏輯留在 application 層做。
     */
    Set<MenuId> findGrantedMenuIds(Set<RoleId> roleIds);
}
