package com.tengan.mall.admin.domain.repository;

import com.tengan.mall.admin.domain.model.MenuId;
import com.tengan.mall.admin.domain.model.Role;
import com.tengan.mall.admin.domain.model.RoleId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(RoleId id);

    List<Role> findByIds(Set<RoleId> ids);

    Optional<Role> findByCode(String roleCode);

    boolean existsByCode(String roleCode);

    /** 角色數量通常不多（相對 admin_user），不做分頁，給下拉選單/管理頁面直接用。 */
    List<Role> findAll();

    /** 選單被刪除時清掉所有角色對它的授權，避免 role_menu 留下指向不存在選單的孤兒列。 */
    void unassignMenuFromAllRoles(MenuId menuId);
}
